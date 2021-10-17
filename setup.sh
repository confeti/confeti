for i in "$@"
do
case $i in
    -i=*|--client_id=*)
    ASTRA_DB_USERNAME="${i#*=}"
    shift
    ;;
    -s=*|--client_secret=*)
    ASTRA_DB_PASSWORD="${i#*=}"
    ;;
    -t=*|--token=*)
    ASTRA_DB_APPLICATION_TOKEN="${i#*=}"
    ;;
esac
done

DBS=$(curl -s --request GET \
  --url "https://api.astra.datastax.com/v2/databases?include=nonterminated&provider=all&limit=25" \
  --header "authorization: Bearer ${ASTRA_DB_APPLICATION_TOKEN}" \
  --header 'content-type: application/json')

DB_ID=$(echo "${DBS}" | jq -c '.[0].id')
DB_REGION=$(echo "${DBS}" | jq -c '.[0].info.region')

DB_KEYSPACE=$(echo "${DBS}" | jq -c '.[0].info.keyspaces[0]')
DB_SECURE_BUNDLE_URL=$(echo "${DBS}" | jq -c '.[0].info.datacenters[0].secureBundleUrl')

export ASTRA_SECURE_BUNDLE_URL=${DB_SECURE_BUNDLE_URL}
gp env ASTRA_SECURE_BUNDLE_URL=${DB_SECURE_BUNDLE_URL} &>/dev/null

# Download the secure connect bundle
curl -s -L $(echo $DB_SECURE_BUNDLE_URL | sed "s/\"//g") -o astra-creds.zip

export ASTRA_DB_BUNDLE="astra-creds.zip"
gp env ASTRA_DB_BUNDLE="astra-creds.zip" &>/dev/null

export ASTRA_DB_KEYSPACE=$(echo ${DB_KEYSPACE} | sed "s/\"//g")
gp env ASTRA_DB_KEYSPACE=$(echo ${DB_KEYSPACE} | sed "s/\"//g") &>/dev/null

export ASTRA_DB_ID=$(echo ${DB_ID} | sed "s/\"//g")
gp env ASTRA_DB_ID=$(echo ${DB_ID} | sed "s/\"//g") &>/dev/null

export ASTRA_DB_REGION=$(echo ${DB_REGION} | sed "s/\"//g")
gp env ASTRA_DB_REGION=$(echo ${DB_REGION} | sed "s/\"//g") &>/dev/null

if [[ -z "$ASTRA_DB_USERNAME" ]]; then
  echo "What is your Astra client id?"
  read -s ASTRA_DB_USERNAME
fi

export ASTRA_DB_USERNAME=${ASTRA_DB_USERNAME}
gp env ASTRA_DB_USERNAME=${ASTRA_DB_USERNAME} &>/dev/null

if [[ -z "$ASTRA_DB_PASSWORD" ]]; then
  echo "What is your Astra client secret?"
  read -s ASTRA_DB_PASSWORD
fi

export ASTRA_DB_PASSWORD=${ASTRA_DB_PASSWORD}
gp env ASTRA_DB_PASSWORD=${ASTRA_DB_PASSWORD} &>/dev/null

echo "The build of the ASTRA environment was completed successfully"
