# COVID19KIT
COVID19KIT is an Android application, it can detect coronavirus in an automated way in x-ray images using the phone's camera or even by uploading an X-ray image from the gallery. The app is equipped with a universal chatbot with the help of Google API and Natural Language Understanding (NLU) platform Dialogflow.

## Table of contents
- [Tech Stack](#tech-stack)
- [Screenshots](#screenshots)
- [Installation](#installation)
- [Author Infos](#author-infos)

---
### Tech Stack
* [x] Android Studio
* [x] Java
* [x] DialogFlow
* [x] TensorFlow
* [x] Google API

---

### Screenshots
 ![](screen.png)
 
---
### Installation
Before we get to dive into the installation process, I just want to mention that my app's brain is a Deep Learning model that I created using Keras in one of my previous projects which you can find [here](https://github.com/zekaouinoureddine/Detecting-COVID-19-in-X-ray-Images).
The Keras model built was converted to a Tflite model using the script below. Therefore, it can be added to an android studio project easily:

```python
    import tensorflow as tf
    model = tf.keras.models.load_model('keras_model_path')
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    open('modelname.tflite', "wb").write(tflite_model)
```

You can get a version of this app on your local machine, so go to your workspace directory and use the git command below:
           
    $ git clone git@github.com:zekaouinoureddine/COVID19KIT.git

---
### Author Infos
- LinkedIn: [Nour Eddine ZEKAOUI](https://www.linkedin.com/in/nour-eddine-zekaoui-ba43b1177/)
---
 
[Back To The Top](#COVID19KIT)
 
