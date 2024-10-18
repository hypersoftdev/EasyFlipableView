[![](https://jitpack.io/v/hypersoftdev/EasyFlipableView.svg)](https://jitpack.io/#hypersoftdev/EasyFlipableView)
# EasyViewFlip

A simple and efficient flip view component to easily create two-sided views, like flipping cards or credit cards, with smooth animations.

## ? Usage

### XML

EasyFlipableView supports both vertical and horizontal flip.

```
// vertical flip
<com.hypersoft.easyviewflip.EasyViewFlip
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	app:flipOnTouch="true"
	app:flipEnabled="true"
	app:flipDuration="400"
	app:flipType="vertical"
	app:flipFrom="front"
	app:autoFlipBack="true"
	app:autoFlipBackTime="1000">

	<!-- Back Layout Goes Here -->
	<include layout="@layout/flash_card_layout_back"/>
        
	<!-- Front Layout Goes Here -->
	<include layout="@layout/flash_card_layout_front"/>

</com.hypersoft.easyviewflip.EasyViewFlip>

// Horizontal flip
<com.hypersoft.easyviewflip.EasyViewFlip
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	app:flipOnTouch="true"
	app:flipEnabled="true"
	app:flipDuration="400"
	app:flipFrom="right"
	app:flipType="horizontal"
	app:autoFlipBack="false">

	<!-- Back Layout Goes Here -->
	<include layout="@layout/flash_card_layout_back"/>

	<!-- Front Layout Goes Here -->
	<include layout="@layout/flash_card_layout_front"/>

</com.hypersoft.easyviewflip.EasyViewFlip>

```

### In Code

```

// Flips the view with or without animation
mYourFlipView.flipTheView();
mYourFlipView.flipTheView(false);

// Sets and Gets the Flip Animation Duration in milliseconds (Default is 400 ms)
mYourFlipView.setFlipDuration(1000);
int dur = mYourFlipView.getFlipDuration();

// Sets and gets the flip enable status (Default is true)
mYourFlipView.setFlipEnabled(false);
boolean flipStatus = mYourFlipView.isFlipEnabled();

// Sets and gets the flip on touch status (Default is true)
mYourFlipView.setFlipOntouch(false);
boolean flipTouchStatus = mYourFlipView.isFlipOnTouch();

// Get current flip state in enum (FlipState.FRONT_SIDE or FlipState.BACK_SIDE)
EasyFlipView.FlipState flipSide = mYourFlipView.getCurrentFlipState();

// Get whether front/back side of flip is visible or not.
boolean frontVal = mYourFlipView.isFrontSide();
boolean backVal = mYourFlipView.isBackSide();

// Get/Set the FlipType to FlipType.Horizontal
boolean isHorizontal = mYourFlipView.isHorizontalType();
mYourFlipView.setToHorizontalType();

// Get/Set the FlipType to FlipType.Vertical
boolean isVertical = mYourFlipView.isVerticalType();
mYourFlipView.setToVerticalType();

// Get/Set if the auto flip back is enabled
boolean isAutoFlipBackEnabled = mYourFlipView.isAutoFlipBack();
mYourFlipView.setAutoFlipBack(true);

// Get/Set the time in milliseconds (ms) after the view is auto flip back to original front side
int autoflipBackTimeInMilliseconds = mYourFlipView.getAutoFlipBackTime();
mYourFlipView.setAutoFlipBackTime(2000);

// Sets the animation direction from left (horizontal) and back (vertical)
easyFlipView.setFlipTypeFromLeft();

// Sets the animation direction from right (horizontal) and front (vertical)
easyFlipView.setFlipTypeFromRight();

// Sets the animation direction from front (vertical) and right (horizontal)
easyFlipView.setFlipTypeFromFront();

// Sets the animation direction from back (vertical) and left (horizontal)
easyFlipView.setFlipTypeFromBack();

// Returns the flip type from direction. For horizontal, it will be either right or left and for vertical, it will be front or back.
easyFlipView.getFlipTypeFrom();

```
### Flip Animation Listener
you can easily listen for the animation completion by setting animation listener.
```

EasyFlipView easyFlipView = (EasyFlipView) findViewById(R.id.easyFlipView);
easyFlipView.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView flipView, EasyFlipView.FlipState newCurrentSide) 
            {
                
                // ...
                // Your code goes here
                // ...
                
            }
        });

```

## Gradle Integration

### Step A: Add Maven Repository

In your project-level **build.gradle** or **settings.gradle** file, add the JitPack repository:
```
repositories {
    google()
    mavenCentral()
    maven { url "https://jitpack.io" }
}
```  

### Step B: Add Dependencies

In your app-level **build.gradle** file, add the library dependency. Use the latest version: [![](https://jitpack.io/v/hypersoftdev/EasyFlipableView.svg)](https://jitpack.io/#hypersoftdev/EasyFlipableView)

Groovy Version
```
 implementation 'com.github.hypersoftdev:EasyFlipableView:x.x.x'
```
Kts Version
```
 implementation("com.github.hypersoftdev:EasyFlipableView:x.x.x")
```

# Acknowledgements

This work would not have been possible without the invaluable contributions of [Farooq Ahmed](https://github.com/ByteSpectra). His expertise, dedication, and unwavering support have been instrumental in bringing this project to fruition.

We are deeply grateful for [Farooq Ahmed](https://github.com/ByteSpectra) involvement and his belief in the importance of this work. His contributions have made a significant impact, and we are honored to have had the opportunity to collaborate with him.

# LICENSE

Copyright 2023 Hypersoft Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
