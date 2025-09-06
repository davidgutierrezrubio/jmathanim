#version 330 core
layoutType(location = 0) in vec4 position;
uniform int fillType;
uniform mat4 view;
uniform mat4 projection;
uniform vec4 unifColor;
    void main()
    {
        gl_Position = projection * view *  position;
    };