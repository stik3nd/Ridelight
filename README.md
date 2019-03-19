## Ridelight

#### UI/UX
I tried to make everything at least pleasant to look at, as I enjoy seeing a beautiful UI with meaningful animations.
I also used some tricks from ConstraintSet to easily animate UI state changes. 

For the flow of the user I tried to make it really simple and hard to make mistakes, as this is a task-oriented app.
I also made the decision to ask for the user destination *first* while I try to make my best guess on the user current location to set the pickup point.

#### Architecture choice

I took this opportunity to try and implement an architecture that has caught my attention, it's called **MVI (Model-View-Intent)**. 

You can check out some great resources on [this blog post series](http://hannesdorfmann.com/android/mosby3-mvi-1) and [this great talk by Jake Wharton](http://jakewharton.com/the-state-of-managing-state-with-rxjava/).

My need to test this architecture was due to frustrations with current options on the Android world that don't take that much advantage of `Functional Programming (immutability, pure functions, etc)`, don't implement an `Unidirectional data flow` and don't have `Reproduucible ui states`.
I was frustrated mainly with **MVP**, as it was my architecture of choice for a long time I've learned it's weaknesses.<br>
The one that worries me the most is that it's **hard to debug errors** due to multiple paths updating the UI at the same time, this is a state hell if your presenter grows too big.

##### Main components
Let's get into **MVI** now. I'll not explain the ideas behind the architecture because the links I mentioned already explain pretty well, I'll focus on how I named things and how it's all connected.

- UiIntent -> This is an intent to change the `UiState` meaning things that the screen/user can do like Refresh data/Change something/Add something.<br>
We create `UiIntents` for things that have any side effect/set something/get something.
- Action -> An `UiIntent` is mapped to an `Action`, they should be different, otherwise we would have a tight coupling between Ui and what Action we should take.<br>
They represent the logic we will apply based on a `UiIntent`.
- Result -> An `Action` always generates a `Result`, mostly to model Success and Failure.
- UiState -> This is the representation of the state of the UI. Based on a `Result` it will decide what the state of the UI is.<br><br>

As you may have noticed the prefix `Ui` is there on purpose, it represents pretty well that there's a separation from the UI and the Action/Result.<br>
Because the UI can go to background and the Action/Result can still be processed and when the UI resubscribes it will get the most recent event.<br>
So these streams are different, since there's a survive mechanism involved.

##### How to connect everything
This is all connected via mainly two methods `processIntents` and `states`, see:

- View (Activity/Fragment/CustomView) -> Emit `UiIntents` and has a `render` method that render the `UiState` every time it receives one.
- ViewModel -> Receives an `Observable` of `UiIntent`s from the View and emits `UiState`s back.<br>

#### Conclusion
I believe the **MVI** architecture is not easy to grasp at first, as it's use of Rx is really heavy. It makes the app looks like it's over-engineered but this is really just the initial impact the architecture has, as soon as you start messing with it and understanding the flow it's really easy to make changes.
<br>It adds a lot of boilerplate, creating a lot of classes.
<br>Tests were really easy to write.
<br><br>In general I'm more of a KISS guy, but I feel that with this architecture you can have a pretty solid app, if I have a complex app to make I would definitely give this implementation a shot.

#### To improve
Probably try to make this architecture easier to understand and simpler to write.


