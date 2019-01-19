This app was originally forked from the **Google TensorFlow codelab** (https://github.com/googlecodelabs/tensorflow-for-poets-2) but now it has diverged into something else:

Most of the Python code related to training and optimizing the TensorFlow AI model is exactly like in Google's TensorFlow codelab. Just small modifications & some shell scripts for convenient retraining of the model.

Most of the changes are related to the actual Android code: Currently the PixBadger app would *search your device for jpg images* and then **classify them with tensorflow** instead of taking image frames from the Camera, like it was done in the original codelab. Last not least code was **converted from Java to Kotlin**. Then the app will display all classified images and classification results in real time in a recylerview.

The UX/UI is minimalistic at the moment as the focus here at the moment is on what is happening "under the hood" at the moment. If I ever decide to move this project in a production ready state I would most likely re-design and re-arrange quite a few things in the user interface and the user flow.

I practice here a lot of cool technologies: **Tensorflow, RxJava2, Viewmodel & LiveData** just to name a few.



**Work-in-progress**
