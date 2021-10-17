
# Android Courier Application
## Firebase Auth/Database/Cloud Functions
It is a **simple** courier application for Android phones.
## Features
1. Sign up and sign in activity with code utilizing [Firebase Authentication](https://firebase.google.com/docs/auth/) (E-mail/Password)
2. [Firebase Realtime Database](https://firebase.google.com/docs/database/) usage for tracking users account details and orders.
3. [Firebase Notifications](https://firebase.google.com/docs/cloud-messaging/) for informing couriers about new orders and clients about delivery status ([Firebase Cloud Functions](https://firebase.google.com/docs/functions/)).
4. Simple e-mail sending function for finished orders (to a defined e-mail address).
5. Users can place orders (Google Places and Maps API).
6. Users can track their current orders.
7. Couriers can change the status of the order they deliver.
8. Users can edit their account informations.
9. Users can lookup their previous orders.
10. There are also simple About and Pricelist Fragments.
11. Navigation drawer ;).

<div><p align="center"><img src="https://user-images.githubusercontent.com/18420040/134238491-a5e95097-2d18-4ddb-96fc-62429f172eb8.png" alt="splash screen" width="480" height="960">
<img src="https://user-images.githubusercontent.com/18420040/134238494-c0a8af17-59da-4442-8831-943cafe91c45.png" alt="Home screen" width="480" height="960">
  <img src="https://user-images.githubusercontent.com/18420040/134238501-a5b49f80-335c-4206-866f-5c3fdcccdd5a.png" alt="Nav bar" width="480" height="960"></p></div>


https://user-images.githubusercontent.com/18420040/137649262-a2f2db58-7a62-49a0-bc91-e8467484e7a7.mp4


https://user-images.githubusercontent.com/18420040/137649298-6d8320ea-2960-47e6-a9c6-17b93acb6e18.mp4



## Disclaimer
The code was written mainly in MVP pattern though probably not in the perfect way (first time using any sort of architectural pattern :)). 

The condition I was trying to satisfy was to isolate Activity/Fragment code from data manipulation and for Presenters to be left without any Android imports.

Code's quality surely could be better. 
