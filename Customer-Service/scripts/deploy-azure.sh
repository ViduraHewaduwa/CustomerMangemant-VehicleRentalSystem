#!/usr/bin/env bash
set -euo pipefail

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
: "${IMAGE_TAG:=latest}"

az account set --subscription "$AZURE_SUBSCRIPTION_ID"

az group create \
  --name "$AZURE_RESOURCE_GROUP" \
  --location "$AZURE_LOCATION" \
  --output none

az deployment group create \
  --resource-group "$AZURE_RESOURCE_GROUP" \
  --template-file "azure/containerapp.bicep" \
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
  --output table
