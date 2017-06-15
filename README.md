# HoloPyramid

This app takes 3D models and projects them on the
screen for use in a hologram pyramid. A hologram pyramid
(really a frustum) is a pyramid-shaped piece of glass or
mirror that can be placed on the screen with the point
upside-down like so:

```
  \            /
   \          /
    \        /     <-- pyramid goes here
     \      /
  --------------   <-- phone flat on table
```

such pyramids are cheap and [easy to make](http://www.instructables.com/id/No-CD-case-no-tape-3D-hologram-pyramid-the-quickes/)

However, the pyramid needs content to use. As a programmer,
I thought it would be cool to write an Android app that uses
OpenGL ES to project a model with the four views needed for
the hologram pyramid.

I also wanted to make the app a little more interactive,
so I used my Bluetooth controller to add manual control
of the model.

## Goals

* Create content for my hologram pyramid
* Try out the Rajawali wrapper library for OpenGL
* Learn the basics of 3D modeling

## Usage

### Hardware

Obviously, this app requires a hologram pyramid. This
can be bought or made by hand.

This app also uses a Bluetooth game controller. This is
not required but is highly recommended.

### Installation
Use Android Studio to compile and install the app to an
Android phone.

### Model Select Activity

The app opens with a list of 3D models to choose
from. Select any of them to go to the hologram projection
mode.

### Hologram Projection Activity

This activity is where the 3D projection actually happens.
Place the pyramid on the center of the phone. There
is a grey square in the center of the screen to help
align the pyramid correctly.

By default, the model rotates automatically.

With a Bluetooth controller connected, the following
buttons let one interact with the model:

| Button | Action |
|--------|--------|
| D-pad left/right    | In manual rotation mode, change the yaw of the model |
| D-pad up/down       | In manual rotation mode, change the pitch of the model |
| A                   | Toggle automatic/manual model rotation |
| B                   | Go back to the model select page |
| Start               | Reset the manual rotation |
| L1/R1               | Adjust the height of the model (to compensate for different screen sizes) |

## How it works

The hologram pyramid projection involves 2 different
scenes. The 3D scene holds thee object to be projected, and the
2D scene is where the four views of the object are
displayed.

### 3D Scene

This is the actual scene that contains the selected
3D model. It consists of the model at the origin
and 4 cameras surrounding it at 90° intervals:

```
 Top down view:
 
            back
                          +----- + x
           cam 2          |
             |            |
             v            +z
   cam 3 -> obj <- cam 1
             ^
             |
            cam 0
 
           front
```

#### Hologram Pyramid Cameras

The cameras used are similar to regular 3D cameras, except for the following
two changes to the projection matrix:

1. The scene is flipped upside down. This is because the views will be
   seen through a physical mirror.
2. The scene is rotated some multiple of 90° counterclockwise. Camera 0 is 
   rotated 0°, Camera 1 is rotated  90° counterclockwise, and so on. This is
   so the object will appear right-side-up in the hologram pyramid.

### 2D Scene

The 2D scene consists of 4 `ScreenQuad`s. Each one represents a single view
of the 3D object. They are textured from `RenderTarget`s (Rajawalli's way of
making off-screen buffers) as explained in the 

Here is the layout of the 2D scene:

```
           Top of screen
            
           quad 2 (cam 2)
 
   quad 3 (cam 3)     quad 1 (cam 1)
 
           quad 0 (cam 0)
           
           Bottom of screen
 
    +y
    |
    |
    |
    +------- +x
```

### Rendering Process

`HoloPyramidRenderer` is a special renderer that renders 4 views of the object.
It does so as follows:

1. First the 3D scene is selected. 
2. For each camera in the 3D scene:
   1. Select the camera
   2. Render the view to the corresponding `RenderTarget`. Each `RenderTarget`
      is used as a texture in the 2D scene.
3. Switch to the 2D scene
4. Render the four quads to the screen. The quads are textured with the
   views from the four `RenderTarget`s