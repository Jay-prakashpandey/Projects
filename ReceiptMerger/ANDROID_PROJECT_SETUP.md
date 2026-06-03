# Receipt Merger Android Project

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── kotlin/com/receiptmerger/app/
│   │   │   ├── MainActivity.kt                 (Entry point)
│   │   │   ├── ui/
│   │   │   │   ├── navigation/
│   │   │   │   │   ├── Screen.kt              (Navigation routes)
│   │   │   │   │   └── NavGraph.kt            (Navigation setup)
│   │   │   │   ├── screens/
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   ├── TemplateSelectionScreen.kt
│   │   │   │   │   ├── MultiFilePickerScreen.kt
│   │   │   │   │   ├── PdfPreviewScreen.kt
│   │   │   │   │   └── SaveShareScreen.kt
│   │   │   │   ├── components/                (Reusable UI components)
│   │   │   │   └── theme/
│   │   │   │       ├── Theme.kt
│   │   │   │       ├── Color.kt
│   │   │   │       └── Type.kt
│   │   │   ├── viewmodel/
│   │   │   │   └── ReceiptMergerViewModel.kt  (State management)
│   │   │   ├── data/
│   │   │   │   └── Models.kt                  (Data classes)
│   │   │   └── utils/
│   │   │       ├── PdfGenerator.kt            (PDF operations)
│   │   │       └── ShareUtils.kt              (Share/Save functions)
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   ├── xml/
│   │   │   │   └── file_paths.xml
│   │   │   └── drawable/
│   │   └── AndroidManifest.xml
│   └── test/
├── build.gradle.kts
└── proguard-rules.pro
```

## Setup Instructions

### 1. Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 34
- Java 17

### 2. Gradle Sync
The `build.gradle.kts` includes all necessary dependencies:
- **Jetpack Compose**: UI framework
- **Material 3**: Design system
- **Navigation Compose**: Screen navigation
- **iText 7**: PDF generation
- **Coil**: Image loading

### 3. Key Files Generated

#### Navigation
- **Screen.kt**: Defines all navigation routes
- **NavGraph.kt**: Sets up Compose navigation with all screens

#### Screens (MVP Features)
1. **HomeScreen**: Entry point with "Start Merging" button
2. **TemplateSelectionScreen**: Select receipt template
3. **MultiFilePickerScreen**: Pick multiple files (images/PDFs)
4. **PdfPreviewScreen**: Preview merged PDF in A4 format
5. **SaveShareScreen**: Save or share the PDF

#### Data & Logic
- **Models.kt**: Receipt and MergeProject data classes
- **ReceiptMergerViewModel.kt**: State management for the app
- **PdfGenerator.kt**: PDF creation and manipulation
- **ShareUtils.kt**: File sharing and saving

#### Theme
- Material 3 theme with custom colors
- Dark/Light mode support
- Responsive typography

## Next Steps (Already Scaffolded, Ready to Implement)

1. **File Picker Implementation**
   - Integrate content resolver for image/PDF selection
   - Add file validation (size, format)

2. **PDF Generation**
   - Complete PdfGenerator.kt with iText operations
   - Implement template layouts for different receipt styles

3. **PDF Preview**
   - Add PDF viewer library (e.g., AndroidPdfViewer)
   - Implement page navigation

4. **Image Processing**
   - Add image compression before PDF merge
   - Implement rotation/crop features

5. **Storage Permissions**
   - Add runtime permission handling for Android 10+
   - Implement scoped storage

6. **Database (Optional)**
   - Add Room for saving projects
   - Implement project history

## Gradle Dependencies Included

```
androidx.compose.ui:ui:1.5.4
androidx.compose.material3:material3:1.1.2
androidx.navigation:navigation-compose:2.7.5
com.itextpdf:itext7-core:7.2.4
io.coil-kt:coil-compose:2.4.0
androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2
```

## Running the App

1. Open in Android Studio
2. Sync Gradle files
3. Select an emulator or device
4. Click "Run" (▶)

The app will launch with the home screen and full navigation flow ready to extend.
