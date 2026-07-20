# Release signing

Release credentials must never be committed to this repository.

## Local setup

1. Copy `keystore.properties.example` to `keystore.properties` if the local file does not exist.
2. Set the keystore path, store password, key alias and key password in that local file.
3. Run `./gradlew assembleRelease` or `./gradlew bundleRelease`.

`keystore.properties`, `*.jks` and `*.keystore` are ignored by Git. Do not remove those ignore rules.

## CI setup

Provide the following environment variables through the CI secret store:

- `RELEASE_STORE_FILE`
- `RELEASE_STORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`

Environment variables take precedence over Gradle properties and the local file.

## Compromised credentials

If signing credentials were committed previously, removing them from the current file is not enough:

1. Rotate the upload key or its credentials as applicable.
2. Remove the secrets from Git history in coordination with every collaborator.
3. Force-push the rewritten history only after making a backup and agreeing on a migration window.
