# jstack-gui

jstack-gui, a launcher of jstack without console.

this project includes:

1. run jstack from gui
2. launch jstack-gui for minecraft as a mod

## How to use

1. enter pid to text field. if you use this as a mod, 
   you don't need to do this step.
2. click "run jstack"
3. now jstack result is on textarea

![gui-image.png](gui-image.png)

## Build time warning

This project will embed tools.jar from jdk.
When you build, please make sure `tools.jar` in your current jvm
is free to redistribution.
