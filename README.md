# NewsApi
An app that fetches latest news . It also allows the users to favourite their news.

Android Architecture Components samples
===================================

A collection of samples using the [Architecture Components](https://developer.android.com/arch):

- [Room](https://developer.android.com/topic/libraries/architecture/room)
- [ViewModels](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
- [RxJava 2](https://github.com/ReactiveX/RxJava)
- [jsonapi-converter](https://github.com/jasminb/jsonapi-converter)
- [Glide](https://github.com/bumptech/glide)
- [Retrofit](https://github.com/square/retrofit) + [OkHttp](https://github.com/square/okhttp)
- [AndroidX](https://developer.android.com/topic/libraries/support-library/androidx-rn)
- [Data Binding](https://developer.android.com/topic/libraries/data-binding/)

# Project Conventions
## MVVM Architecture 
The project follows the MVVM design pattern.<br>
MVVM(Model–view–viewmodel) is one of the architectural patterns which enhances separation of concerns, it allows separating the user interface logic from the business (or the back-end) logic.<br><br>
MVVM has mainly the following layers:
- Model<br>
Model represents the data and business logic of the app. One of the recommended implementation strategies of this layer, is to expose its data through observables to be decoupled completely from ViewModel or any other observer.
- ViewModel<br>
ViewModel interacts with model and also prepares observable(s) that can be observed by a View.
- View<br>
Finally, the view role in this pattern is to observe a ViewModel observable to get data in order to update UI elements accordingly.


## Roadmap

- Kotlin version
- Improving the offline support.
- Adding unit testing

