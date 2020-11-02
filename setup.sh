for i in "$@"
do
case $i in
    -c=*|--credentials=*)
    SERVICE_ACCOUNT="${i#*=}"
    shift
    ;;
    -p=*|--password=*)
    ASTRA_DB_PASSWORD="${i#*=}"
    ;;
esac
done

export SERVICE_ACCOUNT="${SERVICE_ACCOUNT}"

echo "Getting your Astra DevOps API token..."
DEVOPS_TOKEN=$(curl -s --request POST \
  --url "https://api.astra.datastax.com/v2/authenticateServiceAccount" \
  --header 'content-type: application/json' \
  --data "$SERVICE_ACCOUNT" | jq -r '.token')

echo "Getting databases..."
DBS=$(curl -s --request GET \
  --url "https://api.astra.datastax.com/v2/databases?include=nonterminated&provider=all&limit=25" \
  --header "authorization: Bearer ${DEVOPS_TOKEN}" \
  --header 'content-type: application/json')

DB_ID=$(echo "${DBS}" | jq -c '.[0].id')
DB_REGION=$(echo "${DBS}" | jq -c '.[0].info.region')
DB_USER=$(echo "${DBS}" | jq -c '.[0].info.user')

DB_KEYSPACE=$(echo "${DBS}" | jq -c '.[0].info.keyspaces[0]')
DB_SECURE_BUNDLE_URL=$(echo "${DBS}" | jq -c '.[0].info.datacenters[0].secureBundleUrl')

export ASTRA_SECURE_BUNDLE_URL=${DB_SECURE_BUNDLE_URL}
gp env ASTRA_SECURE_BUNDLE_URL=${DB_SECURE_BUNDLE_URL} &>/dev/null

# Download the secure connect bundle
curl -s -L $(echo $DB_SECURE_BUNDLE_URL | sed "s/\"//g") -o astra-creds.zip

export ASTRA_DB_BUNDLE="astra-creds.zip"
gp env ASTRA_DB_BUNDLE="astra-creds.zip" &>/dev/null

export ASTRA_DB_USERNAME=$(echo ${DB_USER} | sed "s/\"//g")
gp env ASTRA_DB_USERNAME=$(echo ${DB_USER} | sed "s/\"//g") &>/dev/null

export ASTRA_DB_KEYSPACE=$(echo ${DB_KEYSPACE} | sed "s/\"//g")
gp env ASTRA_DB_KEYSPACE=$(echo ${DB_KEYSPACE} | sed "s/\"//g") &>/dev/null

export ASTRA_DB_ID=$(echo ${DB_ID} | sed "s/\"//g")
gp env ASTRA_DB_ID=$(echo ${DB_ID} | sed "s/\"//g") &>/dev/null

export ASTRA_DB_REGION=$(echo ${DB_REGION} | sed "s/\"//g")
gp env ASTRA_DB_REGION=$(echo ${DB_REGION} | sed "s/\"//g") &>/dev/null

if [[ -z "$ASTRA_DB_PASSWORD" ]]; then
  echo "What is your Astra DB password?"
  read -s ASTRA_DB_PASSWORD
  export ASTRA_DB_PASSWORD=${ASTRA_DB_PASSWORD}
  gp env ASTRA_DB_PASSWORD=${ASTRA_DB_PASSWORD} &>/dev/null
fi

echo "You're all set"