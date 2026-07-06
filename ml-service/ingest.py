from langchain_core.documents import Document
from config import (
    MONGODB_ATLAS_URI, MONGODB_DATABASE, RECIPE_COLL_NAME,
    VECTOR_INDEX_NAME, vector_collection, vector_store,
)

from langchain_core.documents import Document

docs = []
cursor = vector_collection.database[RECIPE_COLL_NAME].find(
    {}, {"preparationSteps": 1, "title": 1, "duration": 1}
)
for recipe in cursor:
    steps = recipe.get("preparationSteps", [])
    steps_text = "\n\n".join(f"{s['title']}\n{s['content']}" for s in steps)
    title = recipe.get("title", "")
    docs.append(Document(
        page_content=f"{title}\n\n{steps_text}" if title else steps_text,
        metadata={
            "title": title,
            "duration": recipe.get("duration"),
            "recipe_id": str(recipe["_id"]),
        },
    ))

existing_indexes = [idx["name"] for idx in vector_collection.list_search_indexes()]
if VECTOR_INDEX_NAME not in existing_indexes:
    vector_store.create_vector_search_index(dimensions=768, wait_until_complete=45)

vector_collection.delete_many({})
vector_store.add_documents(docs)
print(f"Ingested {len(docs)} recipes.")
