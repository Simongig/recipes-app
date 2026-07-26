FROM ghcr.io/astral-sh/uv:python3.12-bookworm-slim
WORKDIR /app

COPY pyproject.toml uv.lock ./
RUN uv sync --frozen --no-dev

COPY config.py ingest.py main.py models.py retrieve.py ./
RUN mkdir -p logs

EXPOSE 8000
CMD ["uv", "run", "uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
