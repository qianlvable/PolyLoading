# PolyLoading
A simple poly loading animation for android.Inspire by this awesome demo http://tympanus.net/Tutorials/SpringLoaders/

exmaples:

![image](https://github.com/qianlvable/PolyLoading/blob/framebuffer/all_ploy.gif)

In real project

![image](https://github.com/qianlvable/PolyLoading/blob/framebuffer/loading_Rx.gif)

This project contain two version of PolyLoadingView.

PolyLoadingView.java need Facebook Rebound library as dependency,and with some cache feature.

PolyLoadingLiteView.java does not require Rebound! But do not have cache feature.

##Quick start
add dependency in your * app gradle file *:
```gradle

dependencies {
    ....
    compile 'com.lvable.ningjiaqi.polyloading:polyloadingLib:1.1.3'
}

```

If you do not want spring rebound feature , just copy the PolyLoadingView.java and attrs.xml files to your project will do the job
### Jcenter gradle dependency coming soon

### Use it in your layout file ###
```xml
<com.lvable.ningjiaqi.polyloading.PolyLoadingView
  android:id="@+id/poly_loading"
  app:shapeColor="#553322"
  app:edgeCount="6"
  app:depth="3"
  android:layout_width="50dp"
  android:layout_height="50dp" />
```

```Java
PolyLoadingView loadingView = (PolyLoadingView)findViewById(R.id.poly_loading);
loadingView.startLoading();
```

## Attributes ##

*All attributes are optional.*

`edgeCount` specifies the edge count for polygon. *Default: 3*

`shapeColor` specifies the color color of polygon. *Default: 0xff02C39A*

`enableAlpha` specifies enable alpha effect for child polygons. *Default: false*

`filled` specifies filled style or stroke style. *Default: false*

`depth` specifies the count of inscribed polygon inside. *Default: 4*

`tension` specifies the tensiton of the spring animation. (See more on http://facebook.github.io/rebound/) *Default: 20*

`friction` specifies the friction of the spring animation . (See more on http://facebook.github.io/rebound/ )*Default: 6*

## Copyright Notice ##
``` 
Copyright (C) 2016 Ningjiaqi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 ```
