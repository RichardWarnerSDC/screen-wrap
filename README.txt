NahVNC is a recursive acronym standing for Not Another Horrible VNC and aims to be a computer input sharing system like the popular Synergy application. It is written in Java and will utilise the Java Native Interface to provide non-native Java functions. In its very early stages, it currently offers local mouse-wrapping between display edges for any given screen configuration and draws a preview of the total screen real-estate available on a machine with the current location of the mouse. 


Next steps:

Server/Client architecture - have server handle mouse input between clients.

Communication protocol – efficiently transfer mouse and client environment information to server. Server to transfer mouse events to connected client. Only one client active at a time via hasFocus Boolean.


You can download and try out binaries of the application from my OneDrive (no login required):

https://1drv.ms/f/s!AvA0Op4JPut81khiGh6IL2rr8KfE
