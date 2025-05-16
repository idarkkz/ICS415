# 🧱 Minecraft-like Java Game

This project is a minimalist **Minecraft-like voxel engine** written in Java. It includes essential features such as block placement/destruction, terrain rendering, basic lighting, player movement with physics, and a modular game loop. It serves as both a sandbox environment and an educational tool for 3D game engine development.

## 🚀 Features

- 🧊 **Voxel Block World**
  - Block placement & destruction
  - Procedural terrain generation
  - Simple texture support

- 🎮 **Player Movement & Physics**
  - Walking and jumping
  - Camera with look and movement control (WASD + mouse)

- 💡 **Lighting**
  - Directional, Point, and Spot lighting
  - Basic shading using GLSL shaders

- 🖥️ **Rendering Engine**
  - Modular rendering pipeline (Entity + Terrain)
  - ObjectLoader for textured cubes
  - Scene management with transformation utilities

## 🛠️ Technologies Used

| Component      | Technology       |
|----------------|------------------|
| Language       | Java             |
| Build System   | Gradle           |
| Rendering      | LWJGL (OpenGL)   |
| Shaders        | GLSL             |
| Window/Input   | GLFW             |
| Textures       | PNG via STB      |

## 🗃️ Project Structure

```
project3/
├── src/
│   └── main/java/
│       ├── com/devgenie/core/          # Core engine components
│       ├── com/devgenie/core/entity/   # Scene, models, camera, terrain
│       ├── com/devgenie/core/lighting/ # Light sources
│       ├── com/devgenie/core/rendering/# Render pipeline
│       ├── com/devgenie/core/utils/    # Constants, math helpers
│       ├── com/devgenie/test/          # Launcher and demo logic
│       └── org/example/                # Entry point
├── shaders/                            # GLSL shader files
├── textures/                           # Texture images (block faces)
├── build.gradle                        # Gradle build file
└── settings.gradle                     # Gradle settings
```

## 💻 How to Run

### Prerequisites
- Java 17+
- Gradle (or use the included wrapper)
- OpenGL-compatible GPU

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/yourname/minecraft-like-java.git
   cd minecraft-like-java
   ```

2. Run the game:
   ```bash
   ./gradlew run
   ```

3. Controls:
   - **WASD** to move
   - **Mouse** to look around
   - **Left-click** to destroy blocks
   - **Right-click** to place blocks

## 🔧 Customization

You can modify:
- `Terrain.java`: To alter terrain generation
- `ObjectLoader.java`: To support more complex shapes
- `Camera.java`: To tweak camera physics
- `Material.java`: To apply different textures or lighting models

## 📜 License

This project is released under the [MIT License](LICENSE).

---

## 🧠 Credits

Built as a weekend experiment inspired by [Minecraft-Weekend](https://github.com/ands/Minecraft-Weekend) and other open voxel engine projects.
