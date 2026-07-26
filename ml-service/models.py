from pydantic import BaseModel, Field

class MealPlanDay(BaseModel):
    day: str = Field(description="Day of the week")
    breakfast: str = Field(description="Breakfast recipe id")
    lunch: str = Field(description="Lunch recipe id")
    dinner: str = Field(description="Dinner recipe id")

class MealPlanRecipesResponse(BaseModel):
    plan: list[MealPlanDay] = Field(description="List of meal plan days that are relevant to the query")
    decision: str = Field(description="Explanation of the decision made")

    model_config = {
        "json_schema_extra": {
            "examples": [
                {
                    "plan": [
                        {
                            "day": "Monday",
                            "breakfast": "recipe_id_1",
                            "lunch": "recipe_id_2",
                            "dinner": "recipe_id_3"
                        },
                        {
                            "day": "Tuesday",
                            "breakfast": "recipe_id_4",
                            "lunch": "recipe_id_5",
                            "dinner": "recipe_id_6"
                        }
                    ], 
                    "decision": "The meal plan was generated based on the user's preferences and dietary restrictions."
                }
            ]
        }
    }

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

    model_config = {
        "populate_by_name": True,
        "json_schema_extra": {
            "examples": [
                {
                    "jobId": "12345",
                    "ownerId": "user_1",
                    "query": "Can you suggest a weekly meal plan for someone who likes cheese?"
                }
            ]
        }
    }