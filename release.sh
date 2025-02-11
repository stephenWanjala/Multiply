#!/bin/bash

# Load properties file
PROPERTIES_FILE="signing.properties"
NOTES_FILE="release-notes.md"

if [ ! -f "$PROPERTIES_FILE" ]; then
  echo "Error: signing.properties file not found!"
  exit 1
fi

if [ ! -f "$NOTES_FILE" ]; then
  echo "Error: release-notes.md file not found!"
  exit 1
fi

# Read properties
STORE_FILE=$(grep 'storeFile' "$PROPERTIES_FILE" | cut -d'=' -f2 | tr -d '[:space:]')
STORE_PASSWORD=$(grep 'storePassword' "$PROPERTIES_FILE" | cut -d'=' -f2 | tr -d '[:space:]')
KEY_ALIAS=$(grep 'keyAlias' "$PROPERTIES_FILE" | cut -d'=' -f2 | tr -d '[:space:]')
KEY_PASSWORD=$(grep 'keyPassword' "$PROPERTIES_FILE" | cut -d'=' -f2 | tr -d '[:space:]')

# Define APK paths
APK_PATH="app/build/outputs/apk/release/app-release-unsigned.apk"
SIGNED_APK="app/build/outputs/apk/release/app-release-signed.apk"
FINAL_APK="app/build/outputs/apk/release/Multipy.apk"
TAG_NAME="v1.0.0-1-alpha"

# Build unsigned release APK
./gradlew assembleRelease

# Sign the APK
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore "$STORE_FILE" -storepass "$STORE_PASSWORD" \
  -keypass "$KEY_PASSWORD" -signedjar "$SIGNED_APK" "$APK_PATH" "$KEY_ALIAS"

# Rename the signed APK to Multipy.apk
mv "$SIGNED_APK" "$FINAL_APK"

# Align the APK (optional, improves performance)
#zipalign -v 4 "$FINAL_APK" "$FINAL_APK-aligned.apk"

# Install GitHub CLI (if not already installed)
if ! command -v gh &> /dev/null; then
  echo "GitHub CLI not found! Installing..."
  sudo apt install gh -y
fi

# Authenticate with GitHub
#gh auth login

# Create a GitHub Release and upload the renamed APK with release notes
gh release create "$TAG_NAME" "$FINAL_APK" --title "Release $TAG_NAME" --notes-file "$NOTES_FILE"

echo "Release created and Multipy.apk uploaded successfully!"
