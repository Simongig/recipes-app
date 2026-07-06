import logging
logger = logging.getLogger(__name__)

logging.basicConfig(filename='logs/main.log', level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(name)s - %(message)s')
logger.info("ML Service started")

from datetime import datetime, timezone
from fastapi import BackgroundTasks, FastAPI
from retrieve import agent
from models import GenerateJobRequest, QueryRequest
from config import job_collection


app = FastAPI()


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
    job_collection.update_one(
        {"_id": job_id},
        {"$set": {"status": "processing", "updatedAt": datetime.now(timezone.utc)}},
    )
    try:
        result = await agent.ainvoke({"messages": [{"role": "user", "content": query}]})
        structured = result.get("structured_response")
        logger.info("Job %s structured response: %s", job_id, structured)
        job_collection.update_one(
            {"_id": job_id},
            {"$set": {
                "status": "done",
                "recipeIds": structured.recipe_ids if structured else [],
                "decision": structured.decision if structured else None,
                "updatedAt": datetime.now(timezone.utc),
            }},
        )
        logger.info("Job %s done", job_id)
    except Exception as e:
        logger.exception("Job %s failed", job_id)
        job_collection.update_one(
            {"_id": job_id},
            {"$set": {
                "status": "failed",
                "error": str(e),
                "updatedAt": datetime.now(timezone.utc),
            }},
        )


@app.post("/v1/mealplans/generate", status_code=202)
async def generate_meal_plan(request: GenerateJobRequest, background_tasks: BackgroundTasks):
    logger.info("Accepted meal plan job %s for owner %s", request.job_id, request.owner_id)
    background_tasks.add_task(run_generation_job, request.job_id, request.query)
    return {"accepted": True, "jobId": request.job_id}