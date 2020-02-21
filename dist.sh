set -e

./gradlew --no-daemon clean assembleDebug
./gradlew  appDistributionUploadDebug
