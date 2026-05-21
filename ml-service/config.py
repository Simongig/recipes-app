from pymongo import MongoClient
from langchain_mongodb import MongoDBAtlasVectorSearch
from langchain_huggingface import HuggingFaceEmbeddings
from langchain.chat_models import init_chat_model
from dotenv import load_dotenv
import os

load_dotenv("../.env")

MONGODB_ATLAS_URI = os.environ["MONGODB_URI"]
MONGODB_DATABASE  = os.environ["MONGODB_DATABASE"]
HF_TOKEN = os.environ["HF_TOKEN"]


RECIPE_COLL_NAME  = "Recipe"
VECTOR_COLL_NAME  = "vector_store"
VECTOR_INDEX_NAME = "vector_index"

mongo_client      = MongoClient(MONGODB_ATLAS_URI)
vector_collection = mongo_client[MONGODB_DATABASE][VECTOR_COLL_NAME]

embeddings = HuggingFaceEmbeddings(
    model_name="sentence-transformers/all-mpnet-base-v2",
    encode_kwargs={"normalize_embeddings": True},
)

vector_store = MongoDBAtlasVectorSearch(
    embedding=embeddings,
    collection=vector_collection,
    index_name=VECTOR_INDEX_NAME,
    relevance_score_fn="cosine",
)

model = init_chat_model("claude-sonnet-4-6")