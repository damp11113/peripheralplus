# Peripheral+
Adding real world sensor to computercraft! now available for only 1.20.1 forge

# Distance sensor
This sensor will scan the front.

for using it.
```lua
sensor.scan(range, mode)
```
Output: Int of distance

Example
```lua
local sensor = peripheral.find("distance_sensor")
sensor.scan(10, 0)
```
## mode
- 0 = all (Any Entity (include player) + Block)
- 1 = Only block
- 2 = only any entity not player
- 3 = only player
- 4 = only mob
- 5 = any entity include player


# Lidar
Now lidar sensor is compatible with Valkyrien Skies 2 (only blocks detection mode).

Viewer: https://gist.github.com/damp11113/40e2895917965627832225bbe6e5e6b2

Output: Table of detections

Example
```lua
local lidar = peripheral.wrap("top")

lidar.set_range(10)
lidar.set_mode(1)
lidar.set_angle_step(4)
lidar.set_multiline(3)
lidar.set_use_custom_pos(true)

pos = ship.getWorldspacePosition()
rot = ship.getQuaternion()

lidar.set_rotation(rot.x*100, rot.y*100, rot.z*100, rot.w*100, 100)
lidar.set_pos(pos.x, pos.y, pos.z)

lidar.scan()
```
<img width="1657" height="641" alt="image" src="https://github.com/user-attachments/assets/3c275888-6a69-439e-b452-26299335c588" />
<img width="1648" height="583" alt="image" src="https://github.com/user-attachments/assets/43bb068b-8460-46ca-a4d7-26ab38ec9c4b" />

# ccIDE Compatible
Working in progress
