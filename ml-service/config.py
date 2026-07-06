from pymongo import MongoClient
from langchain_mongodb import MongoDBAtlasVectorSearch
from langchain_mongodb.retrievers import MongoDBAtlasHybridSearchRetriever
from langchain_huggingface import HuggingFaceEmbeddings
from langchain.chat_models import init_chat_model
from dotenv import load_dotenv
import os

load_dotenv("../.env")
# Local override: keeps ml-service pointed at the same MongoDB the backend's
# dev profile uses (application-dev.yaml), instead of the Atlas cluster in
# the shared root .env.
load_dotenv(".env", override=True)

MONGODB_ATLAS_URI = os.environ["MONGODB_URI"]
MONGODB_DATABASE  = os.environ["MONGODB_DATABASE"]
HF_TOKEN = os.environ["HF_TOKEN"]


RECIPE_COLL_NAME  = "Recipe"
VECTOR_COLL_NAME  = "vector_store"
VECTOR_INDEX_NAME = "vector_index"
RECIPE_FULLTEXT_INDEX_NAME = "default"
JOB_COLL_NAME     = "MealPlanGenerationJob"

mongo_client      = MongoClient(MONGODB_ATLAS_URI)
vector_collection = mongo_client[MONGODB_DATABASE][VECTOR_COLL_NAME]
# Shared with the Spring backend: it creates the job document and polls it for
# status, this service fills in status/recipeIds/decision/error as it processes it.
job_collection    = mongo_client[MONGODB_DATABASE][JOB_COLL_NAME]

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

retriever = MongoDBAtlasHybridSearchRetriever(
    vectorstore=vector_store,
    search_index_name=RECIPE_FULLTEXT_INDEX_NAME,
    search_fields=["preparationSteps"],
    fulltext_penalty = 50,
    vector_penalty = 50,
    top_k=50
)

model = init_chat_model("claude-sonnet-4-6")