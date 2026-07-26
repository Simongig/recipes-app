import logging
from contextlib import asynccontextmanager

from datetime import datetime, timezone
from fastapi import BackgroundTasks, FastAPI
from retrieve import agent
from models import GenerateJobRequest, MealPlanRecipesResponse, QueryRequest
from config import ML_SERVICE_API_KEY, MealPlanJobStatus, job_collection
import requests

logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logging.basicConfig(filename='logs/main.log', level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(name)s - %(message)s')
    logger.info("ML Service started")
    yield


app = FastAPI(lifespan=lifespan)


@app.get("/")
async def root():
    return {"message": "OK"}

@app.post("/v1/recipe/search-similar")
async def search_recipes(request: QueryRequest):
    logger.info("Search Query: %s", request.query)
    result = await agent.ainvoke({"messages": [{"role": "user", "content": request.query}]})
    structured = result.get("structured_response")
    return {
        "recipe_ids": structured.recipe_ids if structured else [],
        "decision": structured.decision if structured else None,
    }


async def run_generation_job(job_id: str, query: str):
    logger.info("Starting meal plan generation job %s", job_id)
    backend_url = f"http://localhost:8080/api/v1/mealplans/jobs/{job_id}"
    
    header = {"Content-Type": "application/json", "X-ML-Service-API-Key": ML_SERVICE_API_KEY}
    response = requests.patch(backend_url, json={"status": MealPlanJobStatus.PROCESSING.value}, headers=header)
    logger.info("Job %s status updated to PROCESSING, backend response: %s", job_id, response.text)

    try:
        result = await agent.ainvoke({"messages": [{"role": "user", "content": query}]})
        structured:MealPlanRecipesResponse = result.get("structured_response")
        logger.info("Job %s structured response: %s", job_id, structured)
        jobs = job_collection.find({"_id": job_id})
        logger.info("Job %s found in DB: %s", job_id, list(jobs))

        # serialize structured response
        if structured:
            structured_dict = structured.model_dump()

        # call the spring backend
        payload = {
            "status": MealPlanJobStatus.COMPLETED.value,
            "mealPlanDays": structured_dict["plan"] if structured_dict else [],
            "decision": structured_dict["decision"] if structured_dict else None,
        }
        logger.info("Sending payload to backend: %s", payload)
        response = requests.patch(backend_url, json=payload, headers=header)
        logger.info("Backend response: %s", response.text)
        logger.info("Job %s completed", job_id)
    except Exception as e:
        logger.exception("Job %s failed", job_id)
        payload = {
            "status": MealPlanJobStatus.FAILED.value,
            "error": str(e),
        }
        response = requests.patch(backend_url, json=payload, headers=header)


@app.post("/v1/mealplans/generate", status_code=202)
async def generate_meal_plan(request: GenerateJobRequest, background_tasks: BackgroundTasks):
    logger.info("Accepted meal plan job %s for owner %s", request.job_id, request.owner_id)
    background_tasks.add_task(run_generation_job, request.job_id, request.query)
    return {"accepted": True, "jobId": request.job_id}