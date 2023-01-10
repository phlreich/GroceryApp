# Frontend specification grocery ++

## Overview
This repository contains the documentation and implementation of the Grocery++ Frontend App.

Our goal is to build a user friendly interface where the user gets a useful help in shopping. He has the possibility to scan his receipts, create shopping lists and get suggestions based on his past purchases. To use the app, everyone must register and therefore communication with an API is necessary see 
[Grocery API](https://github.com/SE1-WS2020/grocery-api-specification).
Communication with the API gives us the possibility to use registration, login and backup functions.

## Requirements

The app is developed in Android Studio more precisely in Kotlin 1.4.3 with a minimum SDK version of 21 (SDK version 30 is used for testing). The dependencies can be viewed in `app -> Gradle Scripts -> build.gradle(Module:Grocery.app)` and will be installed automatically on first compile.

## Development Set-Up
After cloning the repository and opening the script in Android Studio, the first thing to do is to set up an Android Virtual Device (AVD). The instructions for this can be found here
[Create Android Virtual Device](https://developer.android.com/studio/run/managing-avds).
After creating and selecting an AVD, the code can be executed. The AVD will start and install the app automatically. Once this is done successfully, the app will start.

## Code layout
The app is divided into several files, one for each activity. In the following, each Activiy is explained with its function:
- RegisterActivity.kt: Here the user has the possibility to register. He must specify a username, email and password.
- LoginActivity.kt: With the data provided during registration, the user can log in here. If the login is successful, he lands on the MainActivity.
- MainActivity.kt: The user has here 4 buttons to choose from `Scan receipt`, `Receipt`, `Shopping List` and `Profile`. If he clicks on `Scan receipt` a dialog opens, in which the user has the possibility to take a picture with the camera or to import one from the galery. All other buttons will open the corresponding activity.
- ShoppingListListActivity.kt: Created shopping lists are stored here through a database.
- ReceiptListActivity.kt: Scanned receipts are stored here through a database.
- Profile.kt: The user has the possibility to view his user data (name, email, id) and the possibility to send a backup to the APi or to delete existing backups by clicking the corresponding button.

## Documentation

For communication with the API there is an automatically created documentation in the corresponding README.md in the [Git](https://github.com/SE1-WS2020/grocery-api-specification) of the API. This concerns the functionalities of the `LoginActivity.kt`, `RegisterActivity.kt` and the `Profile.kt`. 
To communicate with the API, [OkHTTP3](https://square.github.io/okhttp/) and [Retrofit](https://square.github.io/retrofit/) were used as clients. To handle the sent and received JSON objects the [GSON Library](https://github.com/google/gson) was integrated.
Receipts was scanned via [Glide](https://bumptech.github.io/glide/doc/generatedapi.html) and to recognize text on the scanned receipts by means of OCR, the [ML Kit](https://developers.google.com/ml-kit/vision/text-recognition/android) from Google was used.
With the help of [Room](https://developer.android.com/training/data-storage/room) scanned receipts as well as the created shopping lists were stored in a database.


