from pydantic import BaseModel, Field

class RecipesResponse(BaseModel):
    recipe_ids: list[str] = Field(description="List of recipe ids that are relevant to the query") 
    decision: str = Field(description="Explanation of the decision made")


class QueryRequest(BaseModel):
    query: str

    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "query": "Can you suggest a weekly meal plan for someone who likes cheese?"
                }
            ]
        }
    }


class GenerateJobRequest(BaseModel):
    # Java sends camelCase (Jackson default); keep snake_case attributes on this side
    # but accept the wire format as-is via aliases.
    job_id: str = Field(alias="jobId")
    owner_id: str = Field(alias="ownerId")
    query: str

    model_config = {"populate_by_name": True}