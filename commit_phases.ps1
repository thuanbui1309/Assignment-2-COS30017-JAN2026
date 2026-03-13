function Safe-Add {
    param([string]$path)
    if (Test-Path $path) { 
        git add $path 
    } else {
        Write-Host "Skipping $path - not found"
    }
}

git reset

# Phase 0
Safe-Add "app/build.gradle.kts"
Safe-Add "app/src/main/AndroidManifest.xml"
Safe-Add "app/src/main/res/values/strings.xml"
Safe-Add "app/src/main/res/values/themes.xml"
Safe-Add "gradle/libs.versions.toml"
git commit -m "build: configure project setup and xml viewbinding"

# Phase 1
Safe-Add "app/src/main/java/com/example/assignment_2_cos30017_jan2026/model/"
Safe-Add "app/src/main/java/com/example/assignment_2_cos30017_jan2026/repository/"
git commit -m "feat: implement data layer with model and repository"

# Phase 2
Safe-Add "app/src/main/java/com/example/assignment_2_cos30017_jan2026/viewmodel/"
git commit -m "feat: add viewmodels for car and rent screens"

# Phase 3
Safe-Add "app/src/main/java/com/example/assignment_2_cos30017_jan2026/MainActivity.kt"
Safe-Add "app/src/main/java/com/example/assignment_2_cos30017_jan2026/adapter/"
Safe-Add "app/src/main/java/com/example/assignment_2_cos30017_jan2026/fragment/"
Safe-Add "app/src/main/res/layout/activity_main.xml"
Safe-Add "app/src/main/res/layout/fragment_car_page.xml"
Safe-Add "app/src/main/res/layout/fragment_favourites.xml"
git commit -m "feat: create home screen with car gallery and favourites"

# Phase 4
Safe-Add "app/src/main/java/com/example/assignment_2_cos30017_jan2026/ui/"
Safe-Add "app/src/main/res/layout/activity_rent.xml"
Safe-Add "app/src/main/res/layout/activity_detail.xml"
git commit -m "feat: implement rent screen and booking flow"

# Phase 5
Safe-Add "app/src/main/res/values-night/"
Safe-Add "app/src/main/java/com/example/assignment_2_cos30017_jan2026/util/ThemeHelper.kt"
git commit -m "feat: add dark mode support"

# Phase 6
Safe-Add "app/src/main/res/values/styles.xml"
Safe-Add "app/src/main/res/values/dimens.xml"
Safe-Add "app/src/main/res/drawable/"
Safe-Add "app/src/main/res/values-sw600dp/"
Safe-Add "app/src/main/res/layout-land/"
git commit -m "style: define reusable styles and drawables"

# Phase 7
Safe-Add "app/src/main/java/com/example/assignment_2_cos30017_jan2026/util/ZoomOutPageTransformer.kt"
Safe-Add "app/src/main/res/xml/"
git commit -m "feat: add advanced features like zoom out transformer and intents"

# Phase 8
Safe-Add "app/src/androidTest/"
git commit -m "test: implement ui testing with espresso"

# Phase 9
git add .
git commit -m "feat: refine ux with rented badge and filter bottom sheet"

git push origin master
