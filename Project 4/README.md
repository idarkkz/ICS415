# ✏️ Bézier Curve Editor (Java)

This is an interactive **Bézier Curve Editor** built with Java and LWJGL. It allows users to visualize, manipulate, and animate Bézier curves using intuitive mouse input. The editor serves as an educational tool for understanding the geometric construction of curves and their applications in graphics.

## 🎨 Features

- Interactive control point manipulation with mouse
- Real-time rendering of Bézier curves
- Visualization of curve interpolation and de Casteljau’s algorithm
- Smooth animations and visual guides
- Lightweight and minimal interface

## 🛠️ Technologies Used

| Component       | Technology       |
|------------------|------------------|
| Language         | Java             |
| Build System     | Gradle           |
| Rendering        | LWJGL (OpenGL)   |
| Window & Input   | GLFW             |

## 🗃️ Project Structure

```
beizer-curve/
├── src/
│   └── main/java/org/example/BezierEditor.java  # Main class and rendering logic
├── build.gradle                                  # Build configuration
├── settings.gradle                               # Gradle project settings
└── .gitignore / gradlew / gradlew.bat            # Git and Gradle helpers
```

## 💻 How to Run

### Prerequisites

- Java 17+
- Gradle (or use the included Gradle wrapper)

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/yourname/bezier-curve-editor.git
   cd bezier-curve-editor
   ```

2. Run the application:
   ```bash
   ./gradlew run
   ```

3. Controls:
   - **Left-click** to drag control points
   - **Right-click** to add or remove control points (if supported)
   - Curve updates live as points are manipulated

## 📚 Educational Value

This tool is designed to:
- Demonstrate **de Casteljau’s algorithm** for Bézier curve interpolation
- Show how curves are formed from control points
- Help users intuitively grasp curve behavior

## 📜 License

This project is licensed under the [MIT License](LICENSE).

## 🙌 Credits

Inspired by classic Bézier curve visualizations and educational demos in computer graphics courses.
