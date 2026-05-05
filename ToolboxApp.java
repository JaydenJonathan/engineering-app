import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

// Timer will be javax.swing.Timer automatically

/*
    Restored by Jayden + Qwen. Your vision, intact.
*/

// fuck no i aint studying for PAK(pendidikan agama kristen/天主教学习的)
// all i freaking know is that we should serve God
// FFS why wont anyone let me code normally
// i forgot my pak book at school nooooooooo ERROR_MESSAGE
//

// ============================================
// ROCKET STABILITY CALCULATOR PANEL
// ============================================
class RocketStabilityPanel extends JPanel {

    private JTextField noseLenField, bodyLenField, diamField, finAreaField;
    private JTextField noseWeightField, bodyWeightField, finWeightField, motorWeightField;
    private JLabel cgLabel, cpLabel, marginLabel, statusLabel;
    private RocketDiagramPanel diagram;

    public RocketStabilityPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 40));

        // Left panel: inputs
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBackground(new Color(30, 30, 40));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Dimensions
        addInputRow(
            inputPanel,
            "Nose Length (cm):",
            noseLenField = new JTextField("15")
        );
        addInputRow(
            inputPanel,
            "Body Length (cm):",
            bodyLenField = new JTextField("60")
        );
        addInputRow(
            inputPanel,
            "Body Diameter (cm):",
            diamField = new JTextField("5")
        );
        addInputRow(
            inputPanel,
            "Fin Area (cm²):",
            finAreaField = new JTextField("30")
        );
        inputPanel.add(new JLabel(" "));
        inputPanel.add(new JLabel(" "));

        // Weights
        addInputRow(
            inputPanel,
            "Nose Weight (g):",
            noseWeightField = new JTextField("30")
        );
        addInputRow(
            inputPanel,
            "Body Weight (g):",
            bodyWeightField = new JTextField("80")
        );
        addInputRow(
            inputPanel,
            "Fin Weight (g):",
            finWeightField = new JTextField("20")
        );
        addInputRow(
            inputPanel,
            "Motor Weight (g):",
            motorWeightField = new JTextField("50")
        );

        // Calculate button
        JButton calcButton = new JButton("🚀 CALCULATE STABILITY");
        calcButton.addActionListener(e -> calculateStability());

        // Results panel
        JPanel resultPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        resultPanel.setBackground(new Color(30, 30, 40));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        cgLabel = createResultLabel("CG: -- cm");
        cpLabel = createResultLabel("CP: -- cm");
        marginLabel = createResultLabel("Static Margin: -- cal");
        statusLabel = createResultLabel("Status: --");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        resultPanel.add(cgLabel);
        resultPanel.add(cpLabel);
        resultPanel.add(marginLabel);
        resultPanel.add(statusLabel);

        // Diagram panel (right side)
        diagram = new RocketDiagramPanel();
        diagram.setPreferredSize(new Dimension(200, 400));
        diagram.setBackground(new Color(20, 20, 30));

        // Assemble left side
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(inputPanel, BorderLayout.CENTER);
        leftPanel.add(calcButton, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(diagram, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }

    private void addInputRow(JPanel panel, String label, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        panel.add(lbl);
        panel.add(field);
    }

    private JLabel createResultLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.CYAN);
        label.setFont(new Font("Monospaced", Font.PLAIN, 14));
        return label;
    }

    private void calculateStability() {
        try {
            // Parse inputs
            double noseLen = Double.parseDouble(noseLenField.getText());
            double bodyLen = Double.parseDouble(bodyLenField.getText());
            double diam = Double.parseDouble(diamField.getText());
            double finArea = Double.parseDouble(finAreaField.getText());
            double noseW = Double.parseDouble(noseWeightField.getText());
            double bodyW = Double.parseDouble(bodyWeightField.getText());
            double finW = Double.parseDouble(finWeightField.getText());
            double motorW = Double.parseDouble(motorWeightField.getText());

            // Assume CG of each part is at its center (simplified)
            // Coordinate origin at nose tip
            double noseCG = noseLen / 2;
            double bodyCG = noseLen + (bodyLen / 2);
            double finCG = noseLen + bodyLen - (bodyLen * 0.1); // Fins near rear
            double motorCG = noseLen + bodyLen - 5; // Motor at very rear

            double totalWeight = noseW + bodyW + finW + motorW;
            double totalMoment =
                (noseW * noseCG) +
                (bodyW * bodyCG) +
                (finW * finCG) +
                (motorW * motorCG);
            double cg = totalMoment / totalWeight; // Center of Gravity from nose tip

            // Center of Pressure (simplified Barrowman)
            // CP = 1/3 of body length from base for low speeds, plus fin effect
            double cpBody = noseLen + ((bodyLen * 2.0) / 3.0); // Approx 66% from nose tip
            double finFactor = finArea / (diam * diam * 10); // Empirically derived
            double cp = cpBody - (finFactor * 30); // Fins move CP rearward (stabilizing)
            if (cp < noseLen) cp = noseLen; // Cannot be in nose cone
            if (cp > noseLen + bodyLen) cp = noseLen + bodyLen;

            double staticMargin = (cp - cg) / diam; // In calibers (1 cal = 1 diameter)

            // Update labels
            cgLabel.setText(String.format("CG: %.1f cm from nose", cg));
            cpLabel.setText(String.format("CP: %.1f cm from nose", cp));
            marginLabel.setText(
                String.format("Static Margin: %.2f cal", staticMargin)
            );

            // Determine stability
            String status;
            Color statusColor;
            if (staticMargin < 0.5) {
                status =
                    "🔴 UNSTABLE (Add more weight to nose or enlarge fins)";
                statusColor = Color.RED;
            } else if (staticMargin < 1.0) {
                status =
                    "🟡 MARGINAL (OK for low power, add nose weight for safety)";
                statusColor = Color.YELLOW;
            } else if (staticMargin <= 2.0) {
                status = "🟢 STABLE (Good flight characteristics)";
                statusColor = Color.GREEN;
            } else {
                status = "🟣 OVERSTABLE (May weathercock into wind)";
                statusColor = Color.MAGENTA;
            }
            statusLabel.setText("Status: " + status);
            statusLabel.setForeground(statusColor);

            // Update diagram
            diagram.setCGandCP(cg, cp, diam, noseLen + bodyLen);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Enter valid numbers!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Inner class for drawing rocket diagram
    // Inner class for drawing rocket diagram (VERTICAL)
    class RocketDiagramPanel extends JPanel {

        private double cg = 50,
            cp = 50,
            totalLen = 100,
            diam = 5;

        public void setCGandCP(
            double cg,
            double cp,
            double diam,
            double totalLen
        ) {
            this.cg = cg;
            this.cp = cp;
            this.totalLen = totalLen;
            this.diam = diam;
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

            int width = getWidth();
            int height = getHeight();

            // Leave margins
            int leftMargin = 60;
            int rightMargin = 60;
            int topMargin = 40;
            int bottomMargin = 60;

            int drawingWidth = width - leftMargin - rightMargin;
            int drawingHeight = height - topMargin - bottomMargin;

            // Scale: rocket width from diameter, rocket height from total length
            double maxRocketWidth = drawingWidth * 0.8; // leave space for fins
            double pixelPerCmWidth = maxRocketWidth / (diam * 2); // diameter twice? actually rocket body width = diam
            double bodyWidthPx = diam * pixelPerCmWidth;
            if (bodyWidthPx < 8) bodyWidthPx = 8;

            double pixelPerCmHeight = drawingHeight / totalLen;
            double rocketHeightPx = totalLen * pixelPerCmHeight;

            // Center horizontally
            int rocketCenterX = width / 2;
            int bodyLeftX = rocketCenterX - (int) (bodyWidthPx / 2);
            int bodyRightX = rocketCenterX + (int) (bodyWidthPx / 2);

            // Rocket bottom Y (fins touch bottom)
            int rocketBottomY = height - bottomMargin;
            int rocketTopY = rocketBottomY - (int) rocketHeightPx;

            // ---- Draw body tube (rectangle) ----
            g2d.setColor(new Color(180, 180, 200));
            g2d.fillRect(
                bodyLeftX,
                rocketTopY,
                bodyRightX - bodyLeftX,
                (int) rocketHeightPx
            );
            g2d.setColor(Color.BLACK);
            g2d.drawRect(
                bodyLeftX,
                rocketTopY,
                bodyRightX - bodyLeftX,
                (int) rocketHeightPx
            );

            // ---- Draw nose cone (triangle on top) ----
            int noseBaseY = rocketTopY;
            int noseTipY = noseBaseY - (int) (0.2 * rocketHeightPx); // nose is 20% of length
            int[] noseX = { bodyLeftX, bodyRightX, rocketCenterX };
            int[] noseY = { noseBaseY, noseBaseY, noseTipY };
            g2d.setColor(new Color(120, 120, 220));
            g2d.fillPolygon(noseX, noseY, 3);
            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(noseX, noseY, 3);

            // ---- Draw fins (at bottom, more realistic) ----
            int finBaseY = rocketBottomY;
            int finHeight = (int) (rocketHeightPx * 0.15);
            int finWidth = (int) (bodyWidthPx * 1.2);
            // Left fin
            // Left fin (trapezoid: wider at bottom, swept back)
            int[] leftFinX = { bodyLeftX, bodyLeftX, bodyLeftX - finWidth };
            int[] leftFinY = { finBaseY, finBaseY - finHeight, finBaseY + 5 };

            // Right fin (triangle pointing right and down)
            int[] rightFinX = { bodyRightX, bodyRightX, bodyRightX + finWidth };
            int[] rightFinY = { finBaseY, finBaseY - finHeight, finBaseY + 5 };

            g2d.setColor(new Color(150, 150, 200));
            g2d.fillPolygon(leftFinX, leftFinY, 3);
            g2d.drawPolygon(leftFinX, leftFinY, 3);
            // Right fin
            g2d.fillPolygon(rightFinX, rightFinY, 3);
            g2d.drawPolygon(rightFinX, rightFinY, 3);
            // Center fin (optional, facing viewer)
            int[] centerFinX = {
                rocketCenterX - 3,
                rocketCenterX + 3,
                rocketCenterX,
            };
            int[] centerFinY = { finBaseY, finBaseY, finBaseY - finHeight };
            g2d.fillPolygon(centerFinX, centerFinY, 3);
            g2d.drawPolygon(centerFinX, centerFinY, 3);

            // ---- Mark CG (green circle) ----
            double cgFraction = cg / totalLen; // from nose tip
            int cgY = rocketTopY + (int) (rocketHeightPx * cgFraction);
            g2d.setColor(Color.GREEN);
            g2d.fillOval(rocketCenterX - 6, cgY - 6, 12, 12);
            g2d.setColor(Color.BLACK);
            g2d.drawString("CG", rocketCenterX - 4, cgY + 4);

            // ---- Mark CP (red circle) ----
            double cpFraction = cp / totalLen;
            int cpY = rocketTopY + (int) (rocketHeightPx * cpFraction);
            g2d.setColor(Color.RED);
            g2d.fillOval(rocketCenterX - 6, cpY - 6, 12, 12);
            g2d.setColor(Color.WHITE);
            g2d.drawString("CP", rocketCenterX - 4, cpY + 4);

            // Draw a line between CG and CP if they are not too close
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawLine(rocketCenterX + 8, cgY, rocketCenterX + 8, cpY);

            int nozzleWidth = (int) (bodyWidthPx * 0.6);
            int nozzleHeight = (int) (rocketHeightPx * 0.08);
            int nozzleLeft = rocketCenterX - nozzleWidth / 2;
            int nozzleTop = rocketBottomY; // directly below body tube
            g2d.setColor(new Color(50, 50, 50));
            g2d.fillRoundRect(
                nozzleLeft,
                nozzleTop,
                nozzleWidth,
                nozzleHeight,
                6,
                6
            );
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(
                nozzleLeft,
                nozzleTop,
                nozzleWidth,
                nozzleHeight,
                6,
                6
            );

            // Optional: engine glow (orange/yellow)
            g2d.setColor(new Color(255, 150, 50, 180)); // semi-transparent orange
            g2d.fillOval(
                rocketCenterX - 8,
                nozzleTop + nozzleHeight - 2,
                16,
                8
            );

            // Title
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString("Rocket Diagram", width / 2 - 50, topMargin - 10);
        }
    }
}

class ReactivityCheck {

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
        int indexA = -1,
            indexB = -1;
        for (int i = 0; i < PLC.length; i++) {
            if (PLC[i].equals(a)) indexA = i;
            if (PLC[i].equals(b)) indexB = i;
        }
        if (indexA == -1 || indexB == -1) return null;
        if (indexA < indexB) return a + " is more reactive than " + b;
        if (indexB < indexA) return b + " is more reactive than " + a;
        return a + " and " + b + " have equal reactivity";
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

class CSVPlotter {

    // Reads a CSV with two columns (x,y) and returns double arrays
    public static double[][] readCSV(File file) throws IOException {
        ArrayList<Double> xList = new ArrayList<>();
        ArrayList<Double> yList = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        boolean firstLine = true;

        while ((line = reader.readLine()) != null) {
            // Skip empty lines
            if (line.trim().isEmpty()) continue;

            // Skip header if present (optional)
            if (
                firstLine &&
                line.toLowerCase().contains("x") &&
                line.toLowerCase().contains("y")
            ) {
                firstLine = false;
                continue;
            }
            firstLine = false;

            String[] parts = line.split(",");
            if (parts.length >= 2) {
                try {
                    double x = Double.parseDouble(parts[0].trim());
                    double y = Double.parseDouble(parts[1].trim());
                    xList.add(x);
                    yList.add(y);
                } catch (NumberFormatException e) {
                    // Skip non-numeric lines
                }
            }
        }
        reader.close();

        // Convert to primitive arrays
        double[] xArr = new double[xList.size()];
        double[] yArr = new double[yList.size()];
        for (int i = 0; i < xList.size(); i++) {
            xArr[i] = xList.get(i);
            yArr[i] = yList.get(i);
        }
        return new double[][] { xArr, yArr };
    }

    // Automatically scales data to fit the graph panel's range (-20 to +20)
    public static double[][] autoScale(double[] xRaw, double[] yRaw) {
        // Find min/max
        double xMin = xRaw[0],
            xMax = xRaw[0];
        double yMin = yRaw[0],
            yMax = yRaw[0];
        for (int i = 1; i < xRaw.length; i++) {
            if (xRaw[i] < xMin) xMin = xRaw[i];
            if (xRaw[i] > xMax) xMax = xRaw[i];
            if (yRaw[i] < yMin) yMin = yRaw[i];
            if (yRaw[i] > yMax) yMax = yRaw[i];
        }

        // Scale to fit -20..20 range
        double xRange = Math.max(Math.abs(xMin), Math.abs(xMax));
        double yRange = Math.max(Math.abs(yMin), Math.abs(yMax));
        double scale = Math.max(xRange, yRange) / 20.0;

        if (scale == 0) scale = 1;

        double[] xScaled = new double[xRaw.length];
        double[] yScaled = new double[yRaw.length];
        for (int i = 0; i < xRaw.length; i++) {
            xScaled[i] = xRaw[i] / scale;
            yScaled[i] = yRaw[i] / scale;
            // Clamp to -20..20
            if (xScaled[i] > 20) xScaled[i] = 20;
            if (xScaled[i] < -20) xScaled[i] = -20;
            if (yScaled[i] > 20) yScaled[i] = 20;
            if (yScaled[i] < -20) yScaled[i] = -20;
        }
        return new double[][] { xScaled, yScaled };
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
        JButton btnCmpr = new JButton("Chemical Comparator");
        JButton btnCircuit = new JButton("⚡ Circuit Editor");
        JButton btnGraph = new JButton("📈 Grapher");
        JButton btnPhysics = new JButton("⚙️ Physics Engine");
        JButton btnQuad = new JButton("x² Quadratics");
        JButton btnCSV = new JButton("📊 CSV Plotter");
        JButton btnRocket = new JButton("rocket stability");
        JButton[] buttons = {
            btnCircuit,
            btnGraph,
            btnPhysics,
            btnQuad,
            btnCmpr,
            btnCSV,
            btnRocket,
        };
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

        btnRocket.addActionListener(e -> {
            contextLabel.setText("🚀 ROCKET STABILITY CALCULATOR");
            middlePanel.removeAll();
            rightPanel.removeAll();

            RocketStabilityPanel rocketPanel = new RocketStabilityPanel();
            rightPanel.add(rocketPanel, BorderLayout.CENTER);
            rightPanel.revalidate();
            rightPanel.repaint();

            // Add some info to middle panel
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));
            JLabel info = new JLabel("📐 Enter rocket dimensions and weights");
            info.setFont(new Font("SansSerif", Font.PLAIN, 12));
            info.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(info);
            JLabel info2 = new JLabel(
                "🎯 Green=CG, Red=CP. Stable if CP is behind CG"
            );
            info2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            info2.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(info2);

            middlePanel.revalidate();
            middlePanel.repaint();
        });
        // === PHYSICS ENGINE ACTION ===
        btnCSV.addActionListener(e -> {
            contextLabel.setText("📁 CSV DATA PLOTTER");
            middlePanel.removeAll();

            // Create file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select CSV file with x,y columns");
            fileChooser.setFileFilter(
                new FileNameExtensionFilter("CSV files", "csv")
            );

            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    double[][] data = CSVPlotter.readCSV(selectedFile);
                    double[] xRaw = data[0];
                    double[] yRaw = data[1];

                    if (xRaw.length == 0) {
                        JOptionPane.showMessageDialog(
                            frame,
                            "No valid data found in CSV!"
                        );
                        return;
                    }

                    // Auto-scale to fit graph
                    double[][] scaled = CSVPlotter.autoScale(xRaw, yRaw);
                    double[] xScaled = scaled[0];
                    double[] yScaled = scaled[1];

                    // Plot on GraphPanel
                    GraphPanel gp = new GraphPanel();
                    rightPanel.removeAll();
                    rightPanel.add(gp, BorderLayout.CENTER);
                    rightPanel.revalidate();
                    rightPanel.repaint();
                    gp.plotData(xScaled, yScaled);

                    // Show info in middle panel
                    middlePanel.removeAll();
                    middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));
                    JLabel info = new JLabel(
                        "📈 Plotted: " + xRaw.length + " points"
                    );
                    info.setFont(new Font("SansSerif", Font.BOLD, 12));
                    info.setAlignmentX(Component.CENTER_ALIGNMENT);
                    middlePanel.add(info);
                    JLabel fileLabel = new JLabel(
                        "File: " + selectedFile.getName()
                    );
                    fileLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
                    fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    middlePanel.add(fileLabel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Error reading file:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }

            middlePanel.revalidate();
            middlePanel.repaint();
        });

        btnCmpr.addActionListener(f -> {
            contextLabel.setText("🧪 CHEMICAL COMPARATOR");
            middlePanel.removeAll();
            rightPanel.removeAll();
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel lbl = new JLabel("🧪 Reactivity Series Comparator");
            lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(lbl);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 30)));

            // Element 1 selector
            JLabel lbl1 = new JLabel("Element 1:");
            lbl1.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(lbl1);

            String[] elements = {
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
            JComboBox<String> elem1 = new JComboBox<>(elements);
            elem1.setMaximumSize(new Dimension(120, 30));
            elem1.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(elem1);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // Element 2 selector
            JLabel lbl2 = new JLabel("Element 2:");
            lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(lbl2);

            JComboBox<String> elem2 = new JComboBox<>(elements);
            elem2.setMaximumSize(new Dimension(120, 30));
            elem2.setAlignmentX(Component.CENTER_ALIGNMENT);
            middlePanel.add(elem2);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 25)));

            // Compare button
            JButton btnCompare = new JButton("🔬 COMPARE");
            btnCompare.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnCompare.setMaximumSize(new Dimension(150, 40));

            // Result label
            JLabel resultLabel = new JLabel(" ");
            resultLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            btnCompare.addActionListener(ev -> {
                ReactivityCheck rc = new ReactivityCheck();
                String e1 = (String) elem1.getSelectedItem();
                String e2 = (String) elem2.getSelectedItem();
                String result = rc.compare(e1, e2);

                if (result != null) {
                    resultLabel.setText(result);
                } else {
                    resultLabel.setText("⚠️ Element not found!");
                }

                // Also show in right panel
                JPanel resultPanel = new JPanel();
                resultPanel.setLayout(
                    new BoxLayout(resultPanel, BoxLayout.Y_AXIS)
                );
                resultPanel.setBackground(new Color(30, 30, 40));

                JLabel titleLabel = new JLabel("⚡ REACTIVITY RESULT ⚡");
                titleLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
                titleLabel.setForeground(Color.CYAN);
                titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel resultDisplay = new JLabel(result);
                resultDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
                resultDisplay.setForeground(Color.WHITE);
                resultDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);

                // Show reactivity series as reference
                JLabel seriesLabel = new JLabel(
                    "Most Reactive → Least Reactive"
                );
                seriesLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
                seriesLabel.setForeground(Color.GRAY);
                seriesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel seriesValues = new JLabel(
                    "K Na Ca Mg Al C Zn Fe Sn Pb H Cu Ag Au"
                );
                seriesValues.setFont(new Font("Monospaced", Font.PLAIN, 10));
                seriesValues.setForeground(new Color(150, 150, 200));
                seriesValues.setAlignmentX(Component.CENTER_ALIGNMENT);

                resultPanel.add(Box.createRigidArea(new Dimension(0, 50)));
                resultPanel.add(titleLabel);
                resultPanel.add(Box.createRigidArea(new Dimension(0, 30)));
                resultPanel.add(resultDisplay);
                resultPanel.add(Box.createRigidArea(new Dimension(0, 40)));
                resultPanel.add(seriesLabel);
                resultPanel.add(seriesValues);

                rightPanel.removeAll();
                rightPanel.add(resultPanel, BorderLayout.CENTER);
                rightPanel.revalidate();
                rightPanel.repaint();
            });

            middlePanel.add(btnCompare);
            middlePanel.add(Box.createRigidArea(new Dimension(0, 20)));
            middlePanel.add(resultLabel);

            middlePanel.revalidate();
            middlePanel.repaint();
        });

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
