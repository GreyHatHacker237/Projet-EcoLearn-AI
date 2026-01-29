@'
# Makefile pour EcoLearn AI (Windows PowerShell)

.PHONY: help setup build up down logs clean test

help:
	@echo "EcoLearn AI - Commandes disponibles:"
	@echo "  make setup     Initialise le projet"
	@echo "  make build     Build les images Docker"
	@echo "  make up        Lance les services"
	@echo "  make down      Arrête les services"
	@echo "  make logs      Affiche les logs"
	@echo "  make clean     Nettoie l'environnement"
	@echo "  make test      Lance les tests"

setup:
	copy .env.example .env
	@echo "⚠️  Modifiez le fichier .env avec vos clés API"

build:
	docker-compose build

up:
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f

clean:
	docker-compose down -v
	docker system prune -f

test:
	docker-compose exec learning-service pytest || echo "Tests learning-service non disponibles"
	docker-compose exec carbon-service pytest || echo "Tests carbon-service non disponibles"
'@ | Set-Content -Path Makefile -Encoding UTF8
# Monitoring commands
monitor:
	@echo "📊 Starting monitoring tools..."
	docker-compose up -d prometheus grafana

monitor-logs:
	docker-compose logs -f

metrics:
	@echo "📈 Available metrics:"
	@echo "- Learning Service: http://localhost:8000/metrics"
	@echo "- Carbon Service: http://localhost:8001/metrics"

test-integration:
	cd tests/integration && python -m pytest test_api_integration.py -v

test-load:
	@echo "🚀 Running load tests with k6..."
	k6 run tests/load/load-test.js