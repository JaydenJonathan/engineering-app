import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import javax.swing.*;

// Timer will be javax.swing.Timer automatically

/*
    Restored by Jayden + Qwen. Your vision, intact.
*/

// fuck no i aint studying for PAK(pendidikan agama kristen/天主教学习的)
// all i freaking know is that we should serve God
// FFS why wont anyone let me code normally
// i forgot my pak book at school nooooooooo ERROR_MESSAGE
//
class ReactivityCheck {

    private String chem1;
    private String chem2;
    private String[] PLC = {
        "K",
        "Na",
        "Ca",
        "Mg",
        "Al",
        "C",
        "Zn",
        "Fe",
        "Sn",
        "Pb",
        "H",
        "Cu",
        "Ag",
        "Au",
    };

    public String compare(String a, String b) {
        int c1 = 0;
        int c2 = 0;

        while (!PLC[c1].equals(a)) {
            c1 += 1;
        }
        while (!PLC[c2].equals(b)) {
            c2 += 1;
        }
        int highest;
        if (c1 > c2) {
            highest = c1;
        } else if (c1 < c2) {
            highest = c2;
        } else {
            highest = c1;
        }

        return PLC[highest];
    }
}

// ============================================
// CELESTIAL ENGINE (Too complex ill  implement it later :P)
// ============================================

class CelestialBody {

    double x, y; // Position (Registers)
    double vx, vy; // Velocity (Momentum)
    double mass; // Gravity Weight
    int size;

    // Constructor: Initialize the 'Register' values
    public CelestialBody(
        double x,
        double y,
        double vx,
        double vy,
        double mass
    ) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.size = (int) Math.max(10, Math.sqrt(mass));
    }

    // Task 1: Implementation of Distance
    public double getDistanceTo(CelestialBody other) {
        // LOGIC: Pythagorean Theorem (a^2 + b^2 = c^2)
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Task 2: Implementation of Force
    public void applyForce(double fx, double fy) {
        // LOGIC: Newton's Second Law (a = F / m)
        this.vx += fx / this.mass;
        this.vy += fy / this.mass;
    }
}

class CelestialPanel extends JPanel implements ActionListener {

    private Point mousePressPoint;
    private Point mouseDragPoint;
    private ArrayList<CelestialBody> bodies = new ArrayList<>();
    private Timer timer;

    public CelestialPanel() {
        // Add a "Sun" (Static heavy mass)
        bodies.add(new CelestialBody(400, 200, 0, 0, 5000));
        // Add a "Planet" (Moving mass)
        bodies.add(new CelestialBody(400, 100, 5, 0, 10));

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // NESTED LOOP: Every body pulls every other body
        for (CelestialBody a : bodies) {
            for (CelestialBody b : bodies) {
                if (a == b) continue; // Don't pull yourself!

                // son why do i have to type everything 😭😭
                double r = a.getDistanceTo(b);
            }
        }

        // Final Step: Update positions
        for (CelestialBody b : bodies) {
            b.x += b.vx;
            b.y += b.vy;
        }
        repaint();
    }

    public void mousePressed(MouseEvent e) {
        mousePressPoint = e.getPoint();
    }

    public void mouseDragged(MouseEvent e) {
        mouseDragPoint = e.getPoint();
        repaint();
    }

    // 3. When you release, CALCULATE THE LAUNCH
    public void mouseReleased(MouseEvent e) {
        double dx = (mousePressPoint.x - e.getX()) * 0.1; // Scale the velocity
        double dy = (mousePressPoint.y - e.getY()) * 0.1;

        // Create the planet with the "flick" velocity
        bodies.add(
            new CelestialBody(mousePressPoint.x, mousePressPoint.y, dx, dy, 10)
        );

        mousePressPoint = null;
        mouseDragPoint = null;
    }

    public void addPlanet(int x, int y) {
        // son im crying 😭(common instagram meme)
        bodies.add(new CelestialBody(x, y, 0, 0, 10));
    }

    public void addStar(int x, int y) {
        // son im crying 😭(common instagram meme)
        bodies.add(new CelestialBody(x, y, 0, 0, 5000));
    }
}

// ============================================
// PHYSICS ENGINE (BUILDING + SIMULATION)
// ============================================
class PhysicsBox {

    double x, y, vx, vy;
    int size = 25;
    Color color;

    public PhysicsBox(double x, double y) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.color = Color.getHSBColor((float) Math.random(), 0.8f, 0.9f);
    }
}

class PhysicsPanel extends JPanel implements ActionListener {

    private ArrayList<PhysicsBox> boxes = new ArrayList<>();
    private Timer timer;
    private double gravity, bounce;
    private int groundY;
    private boolean running = false;

    // Add to PhysicsPanel fields:
    private double windStrength = 0; // Base wind (positive = right)
    private double gustChance = 0; // 0.0 to 1.0 probability per frame
    private double gustStrength = 0; // Extra force when gust hits
    private Random rand = new Random(); // For random gusts

    public PhysicsPanel(
        double g,
        double b,
        int gy,
        double wind,
        double gustProb,
        double gustPow
    ) {
        this.gravity = g;
        this.bounce = b;
        this.groundY = gy;
        this.windStrength = wind;
        this.gustChance = gustProb;
        this.gustStrength = gustPow;
        this.setBackground(new Color(25, 25, 30));
        this.setPreferredSize(new Dimension(450, 450));

        // Click to add boxes
        this.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (running) {
                        boxes.add(new PhysicsBox(e.getX() - 12, e.getY() - 12));
                    }
                }
            }
        );
    }

    public void start() {
        if (!running) {
            running = true;
            timer = new Timer(16, this); // ~60 FPS
            timer.start();
        }
    }

    public void stop() {
        if (timer != null) timer.stop();
        running = false;
    }

    public void reset() {
        boxes.clear();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Physics update
        for (PhysicsBox b : boxes) {
            b.vy += gravity;
            b.y += b.vy;
            b.x += b.vx;

            b.vx += windStrength; // Base wind

            // ✅ NEW: Random gusts
            if (rand.nextDouble() < gustChance) {
                b.vx += (rand.nextBoolean() ? 1 : -1) * gustStrength; // Random direction
            }

            b.x += b.vx;

            // Ground collision
            if (b.y + b.size > groundY) {
                b.y = groundY - b.size;
                b.vy = -b.vy * bounce;
                if (Math.abs(b.vy) < 0.3) b.vy = 0;
            }

            // Box-box collision (simple stacking)
            for (PhysicsBox other : boxes) {
                if (b == other) continue;
                if (
                    b.x < other.x + other.size &&
                    b.x + b.size > other.x &&
                    b.y + b.size > other.y &&
                    b.y < other.y
                ) {
                    b.y = other.y - b.size;
                    b.vy = -b.vy * bounce * 0.5;
                    if (Math.abs(b.vy) < 0.3) b.vy = 0;
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Draw grid
        g2d.setColor(new Color(45, 45, 55));
        for (int i = 0; i < getWidth(); i += 25) g2d.drawLine(
            i,
            0,
            i,
            getHeight()
        );
        for (int i = 0; i < getHeight(); i += 25) g2d.drawLine(
            0,
            i,
            getWidth(),
            i
        );

        // Draw ground
        g2d.setColor(new Color(60, 60, 70));
        g2d.fillRect(0, groundY, getWidth(), getHeight() - groundY);
        g2d.setColor(new Color(100, 100, 120));
        g2d.drawLine(0, groundY, getWidth(), groundY);

        // Draw boxes
        for (PhysicsBox box : boxes) {
            g2d.setColor(box.color);
            g2d.fillRect((int) box.x, (int) box.y, box.size, box.size);
            g2d.setColor(Color.BLACK);
            g2d.drawRect((int) box.x, (int) box.y, box.size, box.size);
        }
    }
}

// ============================================
// CIRCUIT COMPONENTS (RESTORED DETAILS)
// ============================================
class CircuitComponent {

    String type;
    int x, y;
    double rotation;

    public CircuitComponent(String type, int x, int y, double rotation) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public void draw(Graphics2D g2d, int ox, int oy, int cellSize) {
        int px = ox + x * cellSize;
        int py = oy + y * cellSize;

        // Save transform for rotation isolation
        AffineTransform original = g2d.getTransform();
        g2d.rotate(
            Math.toRadians(rotation),
            px + cellSize / 2,
            py + cellSize / 2
        );

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        switch (type) {
            case "Resistor":
                // Zigzag resistor (restored)
                for (int i = 0; i < 4; i++) {
                    g2d.drawLine(px + i * 10, py, px + 5 + i * 10, py - 5);
                    g2d.drawLine(px + 5 + i * 10, py - 5, px + 10 + i * 10, py);
                }
                break;
            case "Capacitor":
                // Two parallel plates (restored)
                g2d.drawLine(px + 5, py - 10, px + 5, py + 10);
                g2d.drawLine(px + 15, py - 10, px + 15, py + 10);
                break;
            case "Voltage Source":
                // Circle with + and - (restored)
                g2d.drawOval(px + 2, py - 10, 16, 16);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2d.drawString("+", px + 7, py - 3);
                g2d.drawString("−", px + 9, py + 8);
                break;
            case "Wire":
            default:
                // Horizontal line (rotates with component)
                g2d.drawLine(
                    px,
                    py + cellSize / 2,
                    px + cellSize,
                    py + cellSize / 2
                );
                break;
        }

        // Restore transform to prevent rotation leak
        g2d.setTransform(original);
    }
}

class CircuitPanel extends JPanel {

    private ArrayList<CircuitComponent> components = new ArrayList<>();
    private final int gridSize = 400;
    private final int cellSize = 20;
    private String selectedType = "Resistor";
    private double currentRotation = 0;

    public CircuitPanel() {
        this.setPreferredSize(new Dimension(450, 450));
        this.setBackground(Color.WHITE);

        this.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int ox = (getWidth() - gridSize) / 2;
                    int oy = (getHeight() - gridSize) / 2;

                    if (
                        e.getX() >= ox &&
                        e.getX() <= ox + gridSize &&
                        e.getY() >= oy &&
                        e.getY() <= oy + gridSize
                    ) {
                        int gridX = (e.getX() - ox) / cellSize;
                        int gridY = (e.getY() - oy) / cellSize;

                        components.add(
                            new CircuitComponent(
                                selectedType,
                                gridX,
                                gridY,
                                currentRotation
                            )
                        );
                        repaint();
                    }
                }
            }
        );
    }

    public void setRotation(double r) {
        currentRotation = r;
    }

    public void setSelectedType(String t) {
        selectedType = t;
    }

    public void clear() {
        components.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ox = (getWidth() - gridSize) / 2;
        int oy = (getHeight() - gridSize) / 2;

        // Draw grid
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= gridSize / cellSize; i++) {
            g2d.drawLine(
                ox + i * cellSize,
                oy,
                ox + i * cellSize,
                oy + gridSize
            );
            g2d.drawLine(
                ox,
                oy + i * cellSize,
                ox + gridSize,
                oy + i * cellSize
            );
        }
        g2d.setColor(Color.BLACK);
        g2d.drawRect(ox, oy, gridSize, gridSize);

        // Draw components
        for (CircuitComponent c : components) {
            c.draw(g2d, ox, oy, cellSize);
        }
    }
}

// ============================================
// GRAPH PANEL (RESTORED SCALING + CLIPPING)
// ============================================
// ============================================
// GRAPH PANEL (DARK THEME + ENHANCED VISUALS)
// ============================================
class GraphPanel extends JPanel {

    private double[] xValues, yValues;
    private final int range = 40; // -20 to +20
    private final int scale = 20; // pixels per unit
    private final int gridSize = range * scale;

    public GraphPanel() {
        this.setPreferredSize(new Dimension(450, 450));
        this.setBackground(new Color(25, 25, 30)); // ✅ Dark background (matches PhysicsPanel)
    }

    public void plotData(double[] x, double[] y) {
        this.xValues = x;
        this.yValues = y;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ox = (getWidth() - gridSize) / 2;
        int oy = (getHeight() - gridSize) / 2;

        // ✅ Draw dark grid (matches PhysicsPanel)
        g2d.setColor(new Color(45, 45, 55)); // Subtle grid lines
        for (int i = 0; i <= range; i++) {
            g2d.drawLine(ox + i * scale, oy, ox + i * scale, oy + gridSize);
            g2d.drawLine(ox, oy + i * scale, ox + gridSize, oy + i * scale);
        }

        // ✅ Draw border
        g2d.setColor(new Color(100, 100, 120));
        g2d.drawRect(ox, oy, gridSize, gridSize);

        // ✅ Draw axes (brighter for contrast)
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(180, 180, 200)); // Light gray axes
        int center = gridSize / 2;
        g2d.drawLine(ox, oy + center, ox + gridSize, oy + center); // X-axis
        g2d.drawLine(ox + center, oy, ox + center, oy + gridSize); // Y-axis

        // ✅ Axis labels
        g2d.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2d.setColor(new Color(200, 200, 220));
        g2d.drawString("(0,0)", ox + 5, oy + center + 15);
        g2d.drawString("20", ox + gridSize - 25, oy + center + 15);
        g2d.drawString("-20", ox + 5, oy + center + 15);
        g2d.drawString("20", ox + center + 5, oy + 15);
        g2d.drawString("-20", ox + center + 5, oy + gridSize - 5);

        // ✅ Draw graph with clipping + glow effect
        if (xValues != null && yValues != null) {
            // Optional: Draw glow shadow first
            g2d.setColor(new Color(70, 130, 230, 100)); // Semi-transparent blue
            g2d.setStroke(new BasicStroke(6)); // Thicker for glow
            g2d.setClip(ox, oy, gridSize, gridSize);

            for (int i = 0; i < xValues.length - 1; i++) {
                int x1 = ox + (int) ((xValues[i] + range / 2.0) * scale);
                int y1 = oy + (int) ((range / 2.0 - yValues[i]) * scale);
                int x2 = ox + (int) ((xValues[i + 1] + range / 2.0) * scale);
                int y2 = oy + (int) ((range / 2.0 - yValues[i + 1]) * scale);
                g2d.drawLine(x1, y1, x2, y2);
            }

            // Draw sharp line on top
            g2d.setColor(new Color(100, 200, 255)); // Bright cyan-blue
            g2d.setStroke(new BasicStroke(2)); // Thin for precision
            for (int i = 0; i < xValues.length - 1; i++) {
                int x1 = ox + (int) ((xValues[i] + range / 2.0) * scale);
                int y1 = oy + (int) ((range / 2.0 - yValues[i]) * scale);
                int x2 = ox + (int) ((xValues[i + 1] + range / 2.0) * scale);
                int y2 = oy + (int) ((range / 2.0 - yValues[i + 1]) * scale);
                g2d.drawLine(x1, y1, x2, y2);
            }
            g2d.setClip(null);
        }
    }
}

// ============================================
// MAIN APP CLASS
// ============================================
public class ToolboxApp {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Engineering IYRC Toolbox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // === LEFT PANEL ===
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(200, 200, 200));
        leftPanel.setPreferredSize(new Dimension(200, 700));
        leftPanel.setBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK)
        );
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel menuLabel = new JLabel("📋 TOOLBOX");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(menuLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JButton btnCircuit = new JButton("⚡ Circuit Editor");
        JButton btnGraph = new JButton("📈 Grapher");
        JButton btnPhysics = new JButton("⚙️ Physics Engine");
        JButton btnQuad = new JButton("x² Quadratics");

        JButton[] buttons = { btnCircuit, btnGraph, btnPhysics, btnQuad };
        for (JButton b : buttons) {
            b.setFont(new Font("SansSerif", Font.PLAIN, 13));
            b.setMaximumSize(new Dimension(180, 40));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftPanel.add(b);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // === MIDDLE PANEL ===
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBackground(new Color(220, 220, 220));
        middlePanel.setPreferredSize(new Dimension(200, 700));
        middlePanel.setBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK)
        );
        middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel contextLabel = new JLabel("🔧 SELECT A TOOL");
        contextLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        contextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        middlePanel.add(contextLabel);

        // === RIGHT PANEL ===
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        // === PHYSICS ENGINE ACTION ===
        btnPhysics.addActionListener(e -> {
            contextLabel.setText("⚙️ PHYSICS ENGINE");
            middlePanel.removeAll();
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel lbl = new JLabel("⚙️ PHYSICS SETTINGS");
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(lbl);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // After the Floor Y field, add:
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));
            JLabel windLabel = new JLabel("🌬️ Wind (→ +):");
            windLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(windLabel);
            JTextField windField = new JTextField("0");
            windField.setColumns(4);
            windField.setMaximumSize(new Dimension(60, 30));
            windField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(windField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 10)));

            JLabel gustLabel = new JLabel("Gust Chance (0-1):");
            gustLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(gustLabel);
            JTextField gustField = new JTextField("0.02"); // 2% chance per frame
            gustField.setColumns(4);
            gustField.setMaximumSize(new Dimension(60, 30));
            gustField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(gustField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 10)));

            JLabel gustPowLabel = new JLabel("Gust Strength:");
            gustPowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(gustPowLabel);
            JTextField gustPowField = new JTextField("2.0");
            gustPowField.setColumns(4);
            gustPowField.setMaximumSize(new Dimension(60, 30));
            gustPowField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(gustPowField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Settings inputs
            JLabel gLabel = new JLabel("Gravity:");
            gLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(gLabel);
            JTextField gField = new JTextField("0.5");
            gField.setColumns(5);
            gField.setMaximumSize(new Dimension(80, 30));
            gField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(gField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 10)));

            JLabel bLabel = new JLabel("Bounce:");
            bLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(bLabel);
            JTextField bField = new JTextField("0.3");
            bField.setColumns(5);
            bField.setMaximumSize(new Dimension(80, 30));
            bField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(bField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 10)));

            JLabel fLabel = new JLabel("Floor Y:");
            fLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(fLabel);
            JTextField fField = new JTextField("400");
            fField.setColumns(5);
            fField.setMaximumSize(new Dimension(80, 30));
            fField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(fField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Start button
            JButton btnStart = new JButton("🚀 START");
            btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnStart.setMaximumSize(new Dimension(100, 35));
            btnStart.addActionListener(ev -> {
                try {
                    double g = Double.parseDouble(gField.getText());
                    double b = Double.parseDouble(bField.getText());
                    int f = Integer.parseInt(fField.getText());
                    double wind = Double.parseDouble(windField.getText()); // ✅ New
                    double gustProb = Double.parseDouble(gustField.getText()); // ✅ New
                    double gustPow = Double.parseDouble(gustPowField.getText()); // ✅ New

                    rightPanel.removeAll();
                    PhysicsPanel pp = new PhysicsPanel(
                        g,
                        b,
                        f,
                        wind,
                        gustProb,
                        gustPow
                    ); // ✅ Pass wind params
                    rightPanel.add(pp, BorderLayout.CENTER);
                    rightPanel.revalidate();
                    rightPanel.repaint();
                    pp.start();

                    System.out.println(
                        "🚀 Physics started: wind=" +
                            wind +
                            ", gusts=" +
                            gustProb
                    );
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Enter valid numbers!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            middlePanel.add(btnStart);

            // Reset button
            JButton btnReset = new JButton("🔄 RESET");
            btnReset.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnReset.setMaximumSize(new Dimension(100, 35));
            btnReset.addActionListener(ev -> {
                Component c = rightPanel.getComponent(0);
                if (c instanceof PhysicsPanel) {
                    ((PhysicsPanel) c).reset();
                }
            });
            middlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
            middlePanel.add(btnReset);

            middlePanel.revalidate();
            middlePanel.repaint();
        });

        // === QUADRATIC GRAPHER ACTION ===
        btnQuad.addActionListener(e -> {
            contextLabel.setText("📈 QUADRATIC GRAPHER");
            middlePanel.removeAll();
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel lbl = new JLabel("📈 y = ax² + bx + c");
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(lbl);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Inputs
            JLabel aLabel = new JLabel("a:");
            aLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(aLabel);
            JTextField aField = new JTextField("1.0");
            aField.setColumns(5);
            aField.setMaximumSize(new Dimension(80, 30));
            aField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(aField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 5)));

            JLabel bLabel = new JLabel("b:");
            bLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(bLabel);
            JTextField bField = new JTextField("0.0");
            bField.setColumns(5);
            bField.setMaximumSize(new Dimension(80, 30));
            bField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(bField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 5)));

            JLabel cLabel = new JLabel("c:");
            cLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(cLabel);
            JTextField cField = new JTextField("0.0");
            cField.setColumns(5);
            cField.setMaximumSize(new Dimension(80, 30));
            cField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(cField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Plot button
            JButton btnPlot = new JButton("📊 PLOT");
            btnPlot.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnPlot.setMaximumSize(new Dimension(100, 35));
            btnPlot.addActionListener(ev -> {
                try {
                    double a = Double.parseDouble(aField.getText());
                    double b = Double.parseDouble(bField.getText());
                    double c = Double.parseDouble(cField.getText());

                    double[] x = new double[41];
                    double[] y = new double[41];
                    for (int i = 0; i <= 40; i++) {
                        x[i] = i - 20; // -20 to +20
                        y[i] = a * x[i] * x[i] + b * x[i] + c;
                    }

                    // Create NEW graph panel for this plot
                    GraphPanel gp = new GraphPanel();
                    rightPanel.removeAll();
                    rightPanel.add(gp, BorderLayout.CENTER);
                    rightPanel.revalidate();
                    rightPanel.repaint();
                    gp.plotData(x, y);

                    System.out.println(
                        "📈 Plotted: y = " + a + "x² + " + b + "x + " + c
                    );
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Enter valid numbers!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            middlePanel.add(btnPlot);

            middlePanel.revalidate();
            middlePanel.repaint();
        });

        // === CIRCUIT EDITOR ACTION ===

        // === CIRCUIT EDITOR ACTION (FIXED) ===
        btnCircuit.addActionListener(evj -> {
            contextLabel.setText("⚡ CIRCUIT EDITOR");
            middlePanel.removeAll();
            rightPanel.removeAll();

            // ✅ Create CircuitPanel instance
            CircuitPanel cp = new CircuitPanel();
            rightPanel.add(cp, BorderLayout.CENTER); // ✅ Add with BorderLayout.CENTER
            rightPanel.revalidate(); // ✅ Recalculate layout
            rightPanel.repaint(); // ✅ Redraw

            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel lbl = new JLabel("⚡ COMPONENTS");
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(lbl);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // ✅ Rotation control (connected to cp)
            JLabel rotLabel = new JLabel("Rotation (°):");
            rotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(rotLabel);

            JTextField rotField = new JTextField("0");
            rotField.setColumns(3);
            rotField.setMaximumSize(new Dimension(60, 30));
            rotField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(rotField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 5)));

            JButton btnSetRot = new JButton("🔄 Set");
            btnSetRot.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnSetRot.setMaximumSize(new Dimension(70, 30));
            btnSetRot.addActionListener(ev -> {
                try {
                    double rot = Double.parseDouble(rotField.getText());
                    cp.setRotation(rot); // ✅ Connected!
                    System.out.println("🔄 Rotation set to: " + rot + "°");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Enter a number!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            middlePanel.add(btnSetRot);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // ✅ Component buttons (connected to cp)
            String[] comps = {
                "Resistor",
                "Capacitor",
                "Voltage Source",
                "Wire",
            };
            for (String comp : comps) {
                JButton btn = new JButton(comp);
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new Dimension(150, 35));
                btn.addActionListener(ev -> {
                    cp.setSelectedType(comp); // ✅ Connected!
                    System.out.println("🔌 Selected: " + comp);
                });
                middlePanel.add(btn);
                middlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }

            // ✅ Clear button (connected to cp)
            JButton btnClear = new JButton("🗑️ Clear");
            btnClear.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnClear.setMaximumSize(new Dimension(150, 35));
            btnClear.addActionListener(ev -> {
                cp.clear(); // ✅ Connected!
                System.out.println("🗑️ Circuit cleared");
            });
            middlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
            middlePanel.add(btnClear);

            middlePanel.revalidate();
            middlePanel.repaint();
        });

        // === LINEAR GRAPHER ACTION ===
        btnGraph.addActionListener(e -> {
            contextLabel.setText("📈 LINEAR GRAPHER");
            middlePanel.removeAll();
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel lbl = new JLabel("📈 y = mx + c");
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(lbl);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel mLabel = new JLabel("m:");
            mLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(mLabel);
            JTextField mField = new JTextField("1.0");
            mField.setColumns(5);
            mField.setMaximumSize(new Dimension(80, 30));
            mField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(mField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 5)));

            JLabel cLabel = new JLabel("c:");
            cLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(cLabel);
            JTextField cField = new JTextField("0.0");
            cField.setColumns(5);
            cField.setMaximumSize(new Dimension(80, 30));
            cField.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(cField);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JButton btnPlot = new JButton("📊 PLOT");
            btnPlot.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnPlot.setMaximumSize(new Dimension(100, 35));
            btnPlot.addActionListener(ev -> {
                try {
                    double m = Double.parseDouble(mField.getText());
                    double c = Double.parseDouble(cField.getText());

                    double[] x = new double[41];
                    double[] y = new double[41];
                    for (int i = 0; i <= 40; i++) {
                        x[i] = i - 20;
                        y[i] = m * x[i] + c;
                    }

                    GraphPanel gp = new GraphPanel();
                    rightPanel.removeAll();
                    rightPanel.add(gp, BorderLayout.CENTER);
                    rightPanel.revalidate();
                    rightPanel.repaint();
                    gp.plotData(x, y);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Enter valid numbers!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });
            middlePanel.add(btnPlot);

            middlePanel.revalidate();
            middlePanel.repaint();
        });

        // === ADD TO FRAME ===
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(middlePanel, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.setVisible(true);
    }
}
