#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BICEP_TEMPLATE="$APP_ROOT/azure/containerapp.bicep"

: "${AZURE_SUBSCRIPTION_ID:?Set AZURE_SUBSCRIPTION_ID}"
: "${AZURE_RESOURCE_GROUP:?Set AZURE_RESOURCE_GROUP}"
: "${AZURE_LOCATION:?Set AZURE_LOCATION}"
: "${ACR_NAME:?Set ACR_NAME}"
: "${LOG_ANALYTICS_NAME:?Set LOG_ANALYTICS_NAME}"
: "${CONTAINERAPPS_ENV_NAME:?Set CONTAINERAPPS_ENV_NAME}"
: "${CONTAINERAPP_NAME:?Set CONTAINERAPP_NAME}"
: "${DB_URL:?Set DB_URL}"
: "${DB_USERNAME:?Set DB_USERNAME}"
: "${DB_PASSWORD:?Set DB_PASSWORD}"
: "${JWT_SECRET:?Set JWT_SECRET}"
: "${EUREKA_ENABLED:=true}"
: "${EUREKA_URL:=}"
: "${IMAGE_TAG:=latest}"
: "${DEPLOYMENT_NAME:=containerapp-$(date -u +%Y%m%d%H%M%S)}"

if [[ "$DB_URL" != jdbc:* ]]; then
  echo "DB_URL must be a JDBC connection string. Current value: '$DB_URL'" >&2
  exit 1
fi

if [[ "$DB_URL" == *"<db-host>"* ]]; then
  echo "DB_URL still contains the placeholder '<db-host>'; set the real Azure MySQL host before deploying." >&2
  exit 1
fi

az account set --subscription "$AZURE_SUBSCRIPTION_ID"

az group create \
  --name "$AZURE_RESOURCE_GROUP" \
  --location "$AZURE_LOCATION" \
  --output none

az deployment group create \
  --name "$DEPLOYMENT_NAME" \
  --resource-group "$AZURE_RESOURCE_GROUP" \
  --template-file "$BICEP_TEMPLATE" \
  --parameters \
      acrName="$ACR_NAME" \
      logAnalyticsWorkspaceName="$LOG_ANALYTICS_NAME" \
      containerAppsEnvName="$CONTAINERAPPS_ENV_NAME" \
      containerAppName="$CONTAINERAPP_NAME" \
      imageTag="$IMAGE_TAG" \
      dbUrl="$DB_URL" \
      dbUsername="$DB_USERNAME" \
      dbPassword="$DB_PASSWORD" \
      jwtSecret="$JWT_SECRET" \
      eurekaEnabled="$EUREKA_ENABLED" \
      eurekaUrl="$EUREKA_URL" \
  --output table
