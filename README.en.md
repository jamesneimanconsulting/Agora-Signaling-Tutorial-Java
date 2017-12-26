# Agora Signaling Tutorial (JAVA)

*其他语言版本： [简体中文](README.md)*
 
This open source sample project demonstrates how to quickly integrate the Agora Signaling SDK for a simple Signal multi-instance or Signal single-instance JAVA chat application.

The following features are included in this sample project:

- Create multiple Signal instances or single Signal instances (the example project has two entries, SingleSignalObjectMain is Signal single instance entry, and MulteSignalObjectMain2 is Signal multi-instance entry)
- Create a target account and log in
- Select chat mode (1. Peer-to-peer private chat 2. Into the channel, multi-person chat group to chat)
- Type in the account name or channel name of the other party (decided by the chat mode of the previous step)
- Show private chat chat history
- Send channel message, receive channel message
- Leave the chat group
- Logout

Agora Signaling SDK supports multiple platforms such as Android/IOS/Linux/MacOS/Web/Windows. You can see sample projects for each platform:

* Android: https://github.com/AgoraIO/Agora-Signaling-Tutorial-Android
* IOS: https://github.com/AgoraIO/Agora-Signaling-Tutorial-iOS-Swift
* Linux: https://github.com/AgoraIO/Agora-Signaling-Tutorial-Linux
* MacOS: https://github.com/AgoraIO/Agora-Signaling-Tutorial-macOS-Swift
* Web: https://github.com/AgoraIO/Agora-Signaling-Tutorial-Web
* Windows https://github.com/AgoraIO/Agora-Signaling-Tutorial-Windows


## Integration mode & run the sample program
* Step 1: Register an account at [Agora.io] (https://dashboard.agora.io/cn/signup/) and create your own test project to get the appId
Then select the Constant.java file in your test project, add appId to the set app_ids, and if you want to implement multiple instances, you will need to add more than one appId.
`` `java
app_ids.add ("Your appId");
`` `
* Step 2: Download the Java version Agora Signaling SDK in [Agora.io SDK] (https://docs.agora.io/cn/2.0.2/download), unzip the jar package under lib folder And libs-dep under the jar package copied to the project's lib file, lib and src folder level.

* Step 3: Add the following code in the dependency property of the build.gradle file in the root directory of the project:

`` `java
 compile fileTree (dir: 'lib', include: ['* .jar'])
`` `
* Step 4: Import the sample project to your development tools as a gradle project.

## Operating environment

* Eclipse or IntelliJ IDEA
* Gradle

## Contact us
- Complete API Documentation See [Documentation Center] (https://docs.agora.io/cn/)
- If you have a problem with integration, you can ask questions on [Developer Community] (https://dev.agora.io/cn/)
- If you have pre-sales consulting questions, you can call 400 632 6626, or join the official Q group 12742516 questions
- If after-sales technical support is required, you can submit a ticket at [Agora Dashboard] (https://dashboard.agora.io)
- If you find a bug in the sample code, please feel free to submit [issue] (https://github.com/AgoraIO/Agora-Android-Tutorial-1to1/issues)

## Code License
The MIT License (MIT).