#!/bin/bash
# scripts/monitor.sh
echo "=== Java API Monitoring ==="
echo "Namespace: java-api-ns-danushka"
echo

echo "Pods Status:"
kubectl get pods -n java-api-ns-danushka
echo -e "\nService Status:"
kubectl get svc -n java-api-ns-danushka
echo -e "\nIngress Status:"
kubectl get ingress -n java-api-ns-danushka
echo -e "\nHPA Status:"
kubectl get hpa -n java-api-ns-danushka
echo -e "\nRecent Events:"
kubectl get events -n java-api-ns-danushka --sort-by='lastTimestamp' | tail -10

read -p "Press [Enter] key to exit..."
