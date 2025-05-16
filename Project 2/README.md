# GLSL Ray Tracer (Shadertoy)

This is a real-time ray tracer implemented in GLSL and designed to run on [Shadertoy](https://www.shadertoy.com/). It features ray-sphere intersection, diffuse and reflective shading, material handling, and a camera with depth of field.

🔗 **Live Demo**: [View on ShaderToy](https://www.shadertoy.com/view/wX23Wd)  

## ✨ Features

- Ray tracing with recursive material bounces
- Lambertian, metal, and dielectric materials
- Procedurally generated ground and sphere grid
- DOF camera with adjustable focus
- Progressive rendering with frame accumulation

## 🚀 How to Use on Shadertoy

If you'd like to run the shader yourself:

1. Visit [the live ShaderToy link](https://www.shadertoy.com/view/wX23Wd)
2. Or manually:
   - Go to [https://www.shadertoy.com](https://www.shadertoy.com)
   - Sign in to your account
   - Click **"New Shader"**
   - Copy and paste the Buffer A code into a new **Buffer A**
   - Copy and paste the Image shader into the **Image** tab
   - Set **Buffer A as iChannel0** on the Image shader
   - Click **▶ Run** to start rendering

## 📄 File Breakdown

- `Buffer A`: Main ray tracing logic with scene setup and recursive ray tracing
- `Image`: Performs gamma correction and final output using data from Buffer A

## ✏️ Customization Tips

- **Add new objects** by modifying the grid loop or the hardcoded `centers[]` list in Buffer A.
- **Change material types** using the constants:
  - `MATERIAL_LAMBERTIAN`
  - `MATERIAL_METAL`
  - `MATERIAL_DIELECTRIC`
- **Adjust camera settings** in the `getRay` function:
  - Position: `camPos`
  - Focus distance: `focusDist`
  - Aperture: `aperture`
  - Field of view: `fov`

## 🗂️ Structure

```
raytracer-shadertoy/
├── shaders/
│   ├── BufferA.glsl           # Ray tracing shader
│   └── Image.glsl             # Post-processing and gamma correction
├── preview.png                # (Optional) Shader preview image
└── LICENSE                    # Open source license
```

## 📜 License

This project is licensed under the [MIT License](LICENSE).
