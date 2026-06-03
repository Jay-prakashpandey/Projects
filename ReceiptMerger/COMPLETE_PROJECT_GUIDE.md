# Receipt Merger - Complete Android Project

A fully functional Android app for merging receipts and images into professional PDF documents using Material 3 and Jetpack Compose.

## 📋 Project Overview

Receipt Merger is a production-ready Android application that allows users to:
- Select from multiple document templates (Standard Receipt, Invoice, Image-to-PDF, PDF Merge, etc.)
- Pick multiple files (images, PDFs) from device storage
- Merge them into a single A4-formatted PDF
- Save to device storage or share directly
- View project history

## 🏗️ Architecture

### Project Structure

```
app/src/main/
├── kotlin/com/receiptmerger/app/
│   ├── MainActivity.kt                    # Entry point with database initialization
│   ├── ui/
│   │   ├── navigation/
│   │   │   ├── Screen.kt                 # Navigation route definitions
│   │   │   └── NavGraph.kt               # Compose navigation setup
│   │   ├── screens/
│   │   │   ├── HomeScreen.kt             # Home/start screen
│   │   │   ├── TemplateSelectionScreen.kt# Template chooser
│   │   │   ├── MultiFilePickerScreen.kt  # File selection
│   │   │   ├── PdfPreviewScreen.kt       # PDF generation
│   │   │   └── SaveShareScreen.kt        # Save/share functionality
│   │   ├── components/
│   │   │   ├── CommonComponents.kt       # Reusable UI components
│   │   │   └── DialogComponents.kt       # Dialog templates
│   │   └── theme/
│   │       ├── Theme.kt                  # Material 3 theme
│   │       ├── Color.kt                  # Color definitions
│   │       └── Type.kt                   # Typography styles
│   ├── viewmodel/
│   │   └── ReceiptMergerViewModel.kt     # Main state management
│   ├── data/
│   │   ├── Models.kt                     # Legacy models (for reference)
│   │   ├── Entities.kt                   # Room database entities
│   │   ├── db/
│   │   │   └── ReceiptMergerDatabase.kt  # Room database instance
│   │   └── dao/
│   │       ├── ReceiptDao.kt             # Receipt data access
│   │       └── MergeProjectDao.kt        # Project data access
│   └── utils/
│       ├── AdvancedPdfGenerator.kt       # PDF generation engine
│       ├── ImageProcessor.kt             # Image compression & processing
│       ├── FileUtils.kt                  # File operations
│       ├── PermissionUtils.kt            # Permission handling
│       ├── PdfGenerator.kt               # Legacy PDF utilities
│       └── ShareUtils.kt                 # Share/save utilities
└── res/
    ├── values/
    │   ├── strings.xml
    │   ├── colors.xml
    │   └── themes.xml
    └── xml/
        ├── file_paths.xml                # FileProvider configuration
        ├── data_extraction_rules.xml     # Data security rules
        └── backup_rules.xml              # Backup configuration
```

## 🔧 Technology Stack

### UI Framework
- **Jetpack Compose** - Modern declarative UI
- **Material 3** - Latest Material Design system
- **Navigation Compose** - In-app navigation

### Data & Storage
- **Room Database** - Local data persistence
- **Flow** - Reactive data streams
- **ViewModel** - Lifecycle-aware state management

### PDF & Document Handling
- **iText 7** - PDF generation and manipulation
- **Compressor** - Image compression before PDF

### Permissions & File Access
- **Accompanist Permissions** - Modern permission handling
- **FileProvider** - Secure file sharing
- **Content Resolver** - File picking

### Build Tools
- Gradle 8.1.2
- Kotlin 1.9.10
- Android SDK 34
- Min SDK 24

## ⚙️ Key Components

### 1. ReceiptMergerViewModel
Central state management handling:
- File selection and management
- Template selection
- PDF generation with progress tracking
- Error handling
- Project persistence

```kotlin
// Usage in screens
val viewModel: ReceiptMergerViewModel = viewModel(
    factory = androidx.lifecycle.viewmodel.ViewModelProvider.Factory { 
        ReceiptMergerViewModel(database) 
    }
)
```

### 2. AdvancedPdfGenerator
Production-ready PDF operations:
- Merge multiple PDFs
- Convert images to PDF with A4 layout
- Create standard receipt templates
- Support for custom templates

```kotlin
// Generate PDF from images
AdvancedPdfGenerator.createReceiptPdfFromImages(
    context, 
    imageUris, 
    outputPath,
    templateType = "standard",
    title = "Receipt"
)

// Merge PDFs
AdvancedPdfGenerator.mergePdfFiles(
    inputFiles,
    outputPath
)
```

### 3. Database Layer
Room database with two main entities:

**MergeProjectEntity** - Project metadata
- id, name, template, createdAt, modifiedAt
- outputPath, status (draft/processing/completed)

**ReceiptEntity** - Individual file records
- projectId (foreign key), filePath, fileName
- fileSize, mimeType, createdAt

### 4. Screen Navigation Flow

```
Home Screen
    ↓
Template Selection (5 options)
    ↓
Multi-File Picker (Get files from device)
    ↓
PDF Preview/Generation (Progress tracking)
    ↓
Save & Share (Save to device or share)
```

## 🎯 Features Implemented

### ✅ Template Selection
- 5 built-in templates:
  - Standard Receipt
  - Image to PDF
  - PDF Merge
  - Invoice Template
  - Receipt with Tax

### ✅ File Handling
- Multi-file selection using Android's file picker
- Support for images (JPG, PNG) and PDFs
- File size display and validation
- Caching strategy for efficient processing

### ✅ PDF Generation
- A4 page format
- Image scaling to fit page
- Header/footer support
- Progress tracking (0-100%)
- Error handling and retry

### ✅ Permissions
- Runtime permissions for Android 6+
- Support for Android 13+ scoped storage
- Graceful permission fallback

### ✅ Save & Share
- Save to Downloads folder
- Direct share via system chooser
- File URI handling with FileProvider
- Success/error notifications

### ✅ Database
- Project history persistence
- Automatic project metadata storage
- Flow-based data updates

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 34
- Java 17
- macOS, Linux, or Windows

### Setup Steps

1. **Clone/Open Project**
   ```bash
   cd /Users/luckyjay/projects/Projects/ReceiptMerger
   ```

2. **Update SDK Path**
   Edit `local.properties`:
   ```
   sdk.dir=/path/to/your/Android/sdk
   ```

3. **Sync Gradle**
   - Open in Android Studio
   - Wait for Gradle sync to complete

4. **Run on Emulator/Device**
   - Select emulator or device
   - Click Run (▶)

## 📦 Dependencies

```gradle
// Jetpack Compose
androidx.compose.ui:ui:1.5.4
androidx.compose.material3:material3:1.1.2
androidx.navigation:navigation-compose:2.7.5

// PDF & Image
com.itextpdf:itext7-core:7.2.4
com.github.barteksc:android-pdf-viewer:3.2.0-beta.1
id.zelory:compressor:3.0.1

// Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// Permissions
com.google.accompanist:accompanist-permissions:0.33.0-alpha

// Image Loading
io.coil-kt:coil-compose:2.4.0

// Lifecycle
androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2
androidx.lifecycle:lifecycle-runtime-compose:2.6.2
```

## 🛠️ Usage Guide

### Basic Flow

1. **Create Project**
   - Tap "Create New Project" on home
   - Select a template
   - Pick files from device
   - Generate PDF
   - Save or share

2. **View Projects**
   - Tap "Recent Projects" to see history
   - Each project shows metadata and status

3. **Share Generated PDF**
   - After generation, tap "Share PDF"
   - Select app to share with
   - Or save to Downloads folder

## 🔐 Security Features

- **FileProvider**: Secure file sharing with URI permissions
- **Permissions**: Runtime permission handling
- **Data Extraction Rules**: Prevents sensitive data leakage
- **ProGuard**: Code obfuscation for release builds

## 🧪 Error Handling

The app includes comprehensive error handling:
- File access errors
- PDF generation failures
- Permission denials
- Storage access issues
- Network timeouts (if needed)

Error messages are shown to user with retry options.

## 📊 State Management

ViewModel manages:
```kotlin
_currentTemplate        // Selected template
_selectedFiles         // Picked files
_isProcessing          // Generation in progress
_processingProgress    // 0.0 - 1.0
_errorMessage          // Error notifications
_generatedPdfPath      // Output file path
_allProjects           // Project history
```

All state flows are observable and update UI reactively.

## 🎨 UI/UX Features

- Material 3 Design System
- Dark/Light theme support (system default)
- Responsive layouts
- Loading indicators
- Error dialogs with retry
- Progress bars
- Empty state messages
- Smooth transitions

## 🔄 Data Flow

```
File Selection → Validation → Caching → PDF Generation
    ↓
Progress Updates (0-100%)
    ↓
Success → Save/Share → Cleanup Cache
    ↓
Database Update
```

## 📝 File Formats Supported

**Input:**
- JPG, PNG, WebP (images)
- PDF (documents)

**Output:**
- PDF (A4 format)

## 🐛 Troubleshooting

### PDF Generation Fails
- Check available storage space
- Verify file permissions
- Try with smaller files first

### Files Not Showing
- Grant storage permissions
- Check file browser compatibility
- Restart app if needed

### Share Not Working
- Ensure FileProvider is properly configured
- Check AndroidManifest.xml includes FileProvider
- Device must support PDF viewing apps

## 📚 Code Examples

### Add File
```kotlin
viewModel.addFile(context, uri)
```

### Generate PDF
```kotlin
viewModel.generatePdf(context, outputDir, "MyReceipt")
```

### Access Projects
```kotlin
val projects by viewModel.allProjects.collectAsState()
```

## 🚢 Build & Release

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease --project-dir=app
```

## 📄 License

This project is part of the e-seller platform suite.

## 🤝 Support

For issues or questions, refer to:
- ANDROID_PROJECT_SETUP.md (Setup guide)
- Inline code documentation
- Material 3 documentation: https://material.io/design

## 🎓 Learning Resources

- Jetpack Compose: https://developer.android.com/jetpack/compose
- Room Database: https://developer.android.com/training/data-storage/room
- Material 3: https://material.io/blog/introducing-material-3
- iText 7: https://itextpdf.com/en/resources/books/itext-7-jumping-into-digital-publishing

---

**Status**: ✅ Complete - Production Ready

**Version**: 1.0.0

**Last Updated**: June 2026
