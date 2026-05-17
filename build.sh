#!/usr/bin/env bash
# -----------------------------------------------------------------------------
# build.sh — Compile and package TravelPlanner without Maven or Gradle.
#
# Usage:
#   bash build.sh          # compile + package
#   bash build.sh clean    # remove out/ and TravelPlanner.jar, then build
#
# Output:
#   TravelPlanner.jar  — runnable with: java -jar TravelPlanner.jar
# -----------------------------------------------------------------------------
set -e

SRC_DIR=src
OUT_DIR=out
JAR=TravelPlanner.jar
MANIFEST=manifest.txt

# ---- 0. Optional clean -------------------------------------------------------
if [ "${1}" = "clean" ]; then
    echo "[clean] Removing $OUT_DIR/ and $JAR"
    rm -rf "$OUT_DIR" "$JAR"
fi

# ---- 1. Prepare output directory ---------------------------------------------
echo "[1/4] Preparing output directory: $OUT_DIR/"
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# ---- 2. Compile all Java sources ---------------------------------------------
echo "[2/4] Compiling Java sources..."
find "$SRC_DIR" -name "*.java" > sources.txt
javac -encoding UTF-8 -d "$OUT_DIR" @sources.txt
rm sources.txt
echo "      $(find "$OUT_DIR" -name '*.class' | wc -l | tr -d ' ') class files written to $OUT_DIR/"

# ---- 3. Copy resources into the class-file tree ------------------------------
echo "[3/4] Copying resources..."
cp -r resources/. "$OUT_DIR"/
echo "      Copied: $(find resources -type f | tr '\n' ' ')"

# ---- 4. Package everything into a JAR ----------------------------------------
echo "[4/4] Packaging $JAR..."
jar cfm "$JAR" "$MANIFEST" -C "$OUT_DIR" .
echo "      JAR size: $(du -sh "$JAR" | cut -f1)"

echo ""
echo "Build successful."
echo "Run with:  java -jar $JAR"
