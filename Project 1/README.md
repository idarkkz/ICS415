# Java Ray Tracer

A modular ray tracer implemented in Java, capable of rendering basic 3D scenes using ray-object intersection, shading models, and PPM image output. This project is structured with clean object-oriented design and supports materials like Lambertian, Metal, and Dielectric.

## ✨ Features

- Recursive ray tracing
- Diffuse and reflective materials
- Refractive surfaces with dielectric materials
- Scene construction using geometric primitives
- Image output in PPM format

## 🧰 Requirements

- Java 11 or later
- A command-line interface or Java IDE (e.g., IntelliJ, Eclipse)

## 🗂️ Directory Structure

```
raytracer/
├── Main.java                   # Entry point; sets up camera, scene, and rendering loop
├── core/                       # Core rendering components (camera, rays, colors)
├── materials/                  # Material types (Lambertian, Metal, Dielectric)
├── math/                       # Math utilities (vectors, intervals)
└── scene/                      # Scene composition and geometry (spheres, lists, hittables)
```

## ▶️ How to Compile and Run

1. **Clone the repository**

```bash
git clone https://github.com/YOUR_USERNAME/raytracer-java.git
cd raytracer-java
```

2. **Compile the source**

```bash
javac -d out $(find . -name "*.java")
```

3. **Run the program**

```bash
java -cp out Main
```

4. **View the rendered image**

The program outputs a file named `image.ppm` in the root directory. Use a PPM-compatible viewer (e.g., GIMP, Photoshop, or online converters) to visualize the result.

## 🎨 Editing the Scene

To customize the rendered scene, open the `Main.java` file and locate the `randomScene()` method. This method defines the scene geometry, camera, and materials. You can:

- Add or remove spheres
- Change material types (`Lambertian`, `Metal`, `Dielectric`)
- Adjust positions, radii, and surface properties

Example:
```java
world.add(new Sphere(new Vec3(0, 1, 0), 1.0, new Dielectric(1.5)));
```

You can also modify image resolution, aspect ratio, samples per pixel, and recursion depth at the top of `Main.java`.

## 📄 License

This project is licensed under the [MIT License](LICENSE).
