from fastapi import FastAPI
from pydantic import BaseModel
from retrieve import agent

app = FastAPI()

class QueryRequest(BaseModel):
    query: str

@app.get("/")
async def root():
    return {"message": "OK"}

@app.post("/v1/recipe/search")
async def search_recipes(request: QueryRequest):
    print("Search Query:", QueryRequest)
    result = await agent.ainvoke({"messages": [{"role": "user", "content": request.query}]})
    return {"answer": result["messages"][-1].content}