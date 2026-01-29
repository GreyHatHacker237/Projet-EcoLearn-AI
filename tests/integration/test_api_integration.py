"""
Integration tests for EcoLearn AI APIs
"""
import pytest
import requests
import time

BASE_URL = "http://localhost:8000"
CARBON_URL = "http://localhost:8001"

def test_learning_service_health():
    """Test that learning service is healthy"""
    response = requests.get(f"{BASE_URL}/health", timeout=5)
    assert response.status_code == 200
    assert response.json()["status"] == "healthy"

def test_carbon_service_health():
    """Test that carbon service is healthy"""
    response = requests.get(f"{CARBON_URL}/health", timeout=5)
    assert response.status_code == 200
    assert response.json()["status"] == "healthy"

def test_services_connected():
    """Test that services can communicate with database"""
    # This test would require database to be running
    pass

def test_api_response_time():
    """Test that API responses are within acceptable time"""
    start_time = time.time()
    response = requests.get(f"{BASE_URL}/health", timeout=5)
    end_time = time.time()
    
    response_time = (end_time - start_time) * 1000  # Convert to ms
    assert response_time < 500  # Should respond in under 500ms