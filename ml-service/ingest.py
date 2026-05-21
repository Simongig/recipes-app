from langchain_community.document_loaders import MongodbLoader
from config import (
    MONGODB_ATLAS_URI, MONGODB_DATABASE, RECIPE_COLL_NAME,
    VECTOR_INDEX_NAME, vector_collection, vector_store,
)
import ast

loader = MongodbLoader(
    connection_string=MONGODB_ATLAS_URI,
    db_name=MONGODB_DATABASE,
    collection_name=RECIPE_COLL_NAME,
    field_names=["preparationSteps"],
    metadata_names=["title", "duration"],
)

data = loader.load()

docs = []
for recipe in data:
    steps = ast.literal_eval(recipe.page_content)
    title = recipe.metadata.get("title", "")
    steps_text = "\n\n".join(f"{step['title']}\n{step['content']}" for step in steps)
    recipe.page_content = f"{title}\n\n{steps_text}" if title else steps_text
    docs.append(recipe)

existing_indexes = [idx["name"] for idx in vector_collection.list_search_indexes()]
if VECTOR_INDEX_NAME not in existing_indexes:
    vector_store.create_vector_search_index(dimensions=768, wait_until_complete=45)

vector_collection.delete_many({})
vector_store.add_documents(docs)
print(f"Ingested {len(docs)} recipes.")
