@echo off
REM .\localkuberstart.bat
kubectl scale deployment/public-info-deployment --replicas=0
docker-compose down
kubectl delete -f k8s

REM Собираем образ и отправляем его в репозиторий Docker Hub
rem mvn clean package
rem mvn clean package -DskipTests

docker-compose up -d
docker build -t doxa80/public-info-app:latest .
docker login
docker push doxa80/public-info-app:latest

REM kubectl port-forward pod/public-info-deployment-64cbb5cc48-xg7b4 8091:8091

REM Очищаем пространство имен и применяем новые манифесты
kubectl apply -f k8s

REM Проверка текущего состояния подов
docker exec -it p_ss_bank_3180-bank-db-1 psql -U postgres -d postgres -c "CREATE SCHEMA IF NOT EXISTS public_info"
kubectl get pods
kubectl port-forward svc/public-info-app-service 8091:8091

rem docker exec p_ss_bank_3180-bank-db-1 pg_dump -U postgres bank-db > backup.sql



