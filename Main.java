import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

class Algorithm{
    private int numQueens;
    private int boardSize;
    private int[][] queensMatrix;
    private ArrayList<ArrayList<Integer>> highlightsArray;
    private ArrayList<JLabel> labelQueenIconsArray;
    private List<List<String>> result;
    private UI ui;
    private int delaymsBetweenHighlights = 50;

    Algorithm(UI ui){
        this.ui = ui;
    };

    public void setdelaymsBetweenHighlights(int input){this.delaymsBetweenHighlights = input;};

    public void displayResult(){
        System.out.println("displayResult()");  // [#Debugging]
        ui.enableInput();
        UIResult UIResult = new UIResult(numQueens, result);
    };
    
    private void displayQueensMatrix(){
        for(int rank = 0; rank<boardSize ; rank++){
            for(int file = 0; file<boardSize ; file++){
                if(queensMatrix[rank][file] == 1)System.out.print("Q ");
                else System.out.print("- ");
            };
            System.out.println();
        };
    };

    private void visuallyPlaceQueen(int indexRank, int indexFile){
        // adding the queen icon
        JLabel panelQueenIcon = new JLabel(ui.getqueenIcon(), SwingConstants.CENTER);
        JPanel squareQueenIcon = ui.getpanelSquares()[indexRank][indexFile];
        
        squareQueenIcon.add(panelQueenIcon);
        
        labelQueenIconsArray.add(panelQueenIcon);
        ui.getframeMain().pack();
    };

    private void visuallyRemoveQueen(){
        if(!labelQueenIconsArray.isEmpty()){
            JLabel labelQueenIcon = labelQueenIconsArray.remove(labelQueenIconsArray.size()-1);
            labelQueenIcon.setVisible(false);
        };
        ui.getframeMain().pack();
    };

    private void highlightSquare(int indexRank, int indexFile, char status){
        // taking note of the position of the highlighted square
        ArrayList<Integer> positionHighlight = new ArrayList<>();
        positionHighlight.add(indexRank);
        positionHighlight.add(indexFile);
        highlightsArray.add(positionHighlight);
        
        JPanel currentSquare = ui.getpanelSquares()[indexRank][indexFile];
        Color squareColor = new Color(0, 200, 0);
        if(status == 'I'){
            squareColor = new Color(200, 0, 0);
        };
        currentSquare.setBackground(squareColor);
        try {
            Thread.currentThread().sleep(delaymsBetweenHighlights);
        } catch (InterruptedException e) {
        }
    };
    private void clearHighlights(){
        for(int index = highlightsArray.size()-1 ; index>=0 ; index--){
            int indexRank = highlightsArray.get(index).get(0);
            int indexFile = highlightsArray.get(index).get(1);

            JPanel currentSquare = ui.getpanelSquares()[indexRank][indexFile];

            Color squareColor;
            Color lightColor = new Color(255, 255, 255);
            Color darkColor = new Color(139, 69, 19);

            if((indexRank+indexFile)%2 == 0){
                squareColor = lightColor;
            }else{
                squareColor = darkColor;
            };
            
            currentSquare.setBackground(squareColor);
        };
    };

    private void highlightCurrentSquare(int indexRank, int indexFile){
        ArrayList<Integer> positionHighlight = new ArrayList<>();
        positionHighlight.add(indexRank);
        positionHighlight.add(indexFile);
        highlightsArray.add(positionHighlight);

        JPanel currentSquare = ui.getpanelSquares()[indexRank][indexFile];
        
        Color squareColor = new Color(255, 255, 0);
        currentSquare.setBackground(squareColor);
    }
    
    private boolean isValidSquare(int rank, int file){
        if(rank>=boardSize || file>=boardSize){
            return false;
        };
        

        // check for other queens on the same file
        for(int pointerRank = rank-1; pointerRank>=0 ; pointerRank--){
            if(queensMatrix[pointerRank][file] == 1){
                // System.out.println("A Queen exists on the same file, at rank " +pointerRank+ " and file " +file);  // [#Debugging]
                highlightSquare(pointerRank, file, 'I');
                return false;
            };
            highlightSquare(pointerRank, file, 'V');
        };
        
        // check for other queens at the upper-left diagonal
        for(int pointerRank=rank-1, pointerFile = file-1; pointerRank>=0 && pointerFile>=0; pointerRank--, pointerFile--){
            if(queensMatrix[pointerRank][pointerFile] == 1){
                // System.out.println("A Queen exists at the top-left diagonal, at rank " +pointerRank+ " and file " +pointerFile);  // [#Debugging]
                highlightSquare(pointerRank, pointerFile, 'I');
                return false;
            };
            highlightSquare(pointerRank, pointerFile, 'V');
        };

        // check for other queens at the upper-right diagonal
        for(int pointerRank=rank-1, pointerFile = file+1; pointerRank>=0 && pointerFile<boardSize; pointerRank--, pointerFile++){
            if(queensMatrix[pointerRank][pointerFile] == 1){
                // System.out.println("A Queen exists at the top-right diagonal, at rank " +pointerRank+ " and file " +pointerFile); // [#Debugging]
                highlightSquare(pointerRank, pointerFile, 'I');
                return false;
            };
            highlightSquare(pointerRank, pointerFile, 'V');
        };
        return true;
    };

    private void Backtrack(int rank, int file, List<String> queensPosition, int queensRemaining){
        // System.out.println("Backtracking, removing last placed Queen from rank " +rank+ " and file " +file);   // [#Debugging]

        // update the queensMatrix to remove the queen
        queensMatrix[rank][file] = 0;

        // update the queensPosition to remove the queen
        if(!queensPosition.isEmpty()){
            queensPosition.remove(queensPosition.size()-1);
            visuallyRemoveQueen();
        };
        // System.out.println("Board Position with " +queensRemaining+ " remaining queens:"); // [#Debugging]
        // displayQueensMatrix();  // [#Debugging]
    }

    private void placeQueen(int rank, int file, List<String> queensPosition, int queensRemaining){
        boolean isValidSquare[] = {false};
        
        Thread threadplaceQueen = new Thread(() -> {
            visuallyPlaceQueen(rank, file);
            highlightCurrentSquare(rank, file);
            isValidSquare[0] = isValidSquare(rank, file);
        });
        threadplaceQueen.start();
        try{
            threadplaceQueen.join();
        }catch(InterruptedException e){
        };

        clearHighlights();
        // base case
        if(!isValidSquare[0]){
            // System.out.println("Therefore, Square is not valid at rank: " +rank+ " and file: " +file);   // [#Debugging]
            visuallyRemoveQueen();
            return;
        }
        

        // update the queensMatrix to add the queen
        queensMatrix[rank][file] = 1;

        // constructing the position of the queen at the current position
        String queenPosition = "";
        for(int i=0 ; i<boardSize ; i++){
            if(i == file)queenPosition += "Q";
            else queenPosition += ".";
        };
        
        // update the queensPosition to add the queen
        queensPosition.add(queenPosition);

        
        // System.out.println("Valid Square at rank: " +rank+ " and file: " +file);   // [#Debugging]
        // displayQueensMatrix();  // [#Debugging]

        // when all queens are placed at valid squares
        if(queensRemaining == 0){
            // System.out.println("Valid Board Position"); // [#Debugging]
            // System.out.println("Adding board position to results"); // [#Debugging]
            List<String> validQueensPosition = new ArrayList<>(queensPosition);
            result.add(validQueensPosition);
            Backtrack(rank, file, queensPosition, queensRemaining+1);
            return;
        };
        
        // place next queen to the next rank if the rank exist
        for(int pointerFile = 0 ; pointerFile<boardSize && rank+1 < boardSize ; pointerFile++){
            // System.out.println("Trying to place the next Queen at rank " +(rank+1)+ " and file: " +pointerFile);    // [#Debugging]
            placeQueen(rank+1, pointerFile, queensPosition, queensRemaining-1);
            // System.out.println();    // [#Debugging]
        };
        // at this point, queens has been placed at all files of the following ranks
        Backtrack(rank, file, queensPosition, queensRemaining-1);
    }
    
    public void runAlgorithm(){
        numQueens = ui.getnumQueens();
        boardSize = numQueens;
        result = new ArrayList<>();
        queensMatrix = new int[numQueens][numQueens];
        highlightsArray = new ArrayList<>();
        labelQueenIconsArray = new ArrayList<>();
        
        System.out.println("Running algorithm for " +numQueens+ " Queens");

        // for every file in the 0th rank
        for(int rank=0, file=0 ; file < numQueens ; file++){
            // a list to store the queens position for the current position
            List<String> queensPosition = new ArrayList<>();
            
            // System.out.println("Placing Queen at rank " +rank+ " and file: " +file);    // [#Debugging]
            placeQueen(0, file, queensPosition, numQueens-1);
        }
        highlightsArray = null;
        labelQueenIconsArray = null;
        displayResult();
    }
};

class UIResult{
    private JFrame frameResult;
    private List<List<String>> result;
    private int numQueens;
    private JPanel [][] panelResultPositionGrid;
    private int maxColumnPresentation = 3;
    private int squareLength = 60;
    private ImageIcon queenIcon;

    UIResult(int numQueens, List<List<String>> result){
        this.numQueens = numQueens;
        this.result = result;
        buildFrameResult();
    };
    
    private boolean squareContainsQueen(int indexRow, int indexColumn, int indexRank, int indexFile){
        List<String> position = result.get((indexRow*maxColumnPresentation) + (indexColumn));
        String rankInfo = position.get(indexRank);
        char squareInfo = rankInfo.charAt(indexFile);
        if(squareInfo == 'Q'){
            return true;
        }
        return false;
    };
    
    private JPanel buildPanelPosition(int indexRow, int indexColumn){
        int boardSize = numQueens;
        int boardLength = numQueens*squareLength;
        Color lightColor = new Color(255, 255, 255);
        Color darkColor = new Color(139, 69, 19);
        
        JPanel panelPosition = new JPanel();
        panelPosition.setLayout(new GridLayout(numQueens, numQueens));
        
        JPanel[][] panelSquares = new JPanel[boardSize][boardSize];
        for(int indexRank=0 ; indexRank<boardSize ; indexRank++){
            for(int indexFile=0 ; indexFile<boardSize ; indexFile++){
                JPanel panelSquare = new JPanel();
                panelSquare.setPreferredSize(new Dimension(squareLength, squareLength));
                panelSquare.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                if((indexRank+indexFile)%2 == 0){
                    panelSquare.setBackground(lightColor);
                }else{
                    panelSquare.setBackground(darkColor);
                };
                if(squareContainsQueen(indexRow, indexColumn, indexRank, indexFile)){
                    panelSquare.add(new JLabel(queenIcon, SwingConstants.CENTER));
                }
                panelPosition.add(panelSquare);
                panelSquares[indexRank][indexFile] = panelSquare;
            };
        };

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperPanel.add(panelPosition);
        return wrapperPanel;
    };
    
    public JPanel buildPanelResultTitle(){
        JPanel panelTitle = new JPanel();
        
        JLabel labelTitle = new JLabel(result.size()+ " Possible Positions");
        
        panelTitle.add(labelTitle);
        
        return panelTitle;
    };

    
    public JPanel buildPanelResultPosition(){
        JPanel panelResultPosition = new JPanel();
        
        int maxRows = result.size()/maxColumnPresentation;
        if(result.size() > maxColumnPresentation*maxRows){++maxRows;}  // to fix issue with 10 possible solutions
        int maxColumns = maxColumnPresentation;

        // for symmetric layout with 1 row results
        if(result.size()<=maxColumnPresentation){maxRows = 1; maxColumns = result.size();}
        
        panelResultPosition.setLayout(new GridLayout(maxRows, maxColumns));
        
        panelResultPositionGrid = new JPanel[maxRows][maxColumns];
        // Initialize each element in the array
        for (int i = 0; i < maxRows; i++) {
            for (int j = 0; j < maxColumns; j++) {
                panelResultPositionGrid[i][j] = new JPanel();
            }
        }

        // image
        
        ImageIcon originalIcon  = new ImageIcon("crown.png");
        Image image = originalIcon .getImage();
        Image scaledImage = image.getScaledInstance(squareLength-20, squareLength-20, Image.SCALE_SMOOTH);
        queenIcon = new ImageIcon(scaledImage);

        for(int indexRow = 0 ; indexRow<maxRows ; indexRow++){
            for(int indexColumn = 0 ; indexColumn<maxColumns && (((indexRow*maxColumnPresentation)+indexColumn)+1)<=result.size() ; indexColumn++){
                // returns the wrapper;
                JPanel gridContent = buildPanelPosition(indexRow, indexColumn);
                panelResultPositionGrid[indexRow][indexColumn].add(gridContent);
                panelResultPosition.add(panelResultPositionGrid[indexRow][indexColumn]);
            };
        };

        return panelResultPosition;
    }

    private void buildFrameResult(){
        frameResult = new JFrame("Result for " +numQueens+ " Queens ("+ result.size() +" Positions)");
        frameResult.setPreferredSize(new Dimension(1366, 720));
        
        // JPanel panelResultTitle = buildPanelResultTitle();
        // Color titleColor = new Color(127, 255, 127);
        // panelResultTitle.setBackground(titleColor);
        // frameResult.add(panelResultTitle);

        JPanel panelResultPosition = buildPanelResultPosition();
        frameResult.add(panelResultPosition);

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperPanel.add(panelResultPosition);
        frameResult.add(wrapperPanel);

        // adding a scrollbar
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        frameResult.add(scrollPane);

        // adjust the vertical scroll speed
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16); // Set unit increment (default is typically 1-16)
        verticalScrollBar.setBlockIncrement(50); // Set block increment (default is typically a larger value)

        // adjust the horizontal scroll speed
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUnitIncrement(16); // Set unit increment (default is typically 1-16)
        horizontalScrollBar.setBlockIncrement(50); // Set block increment (default is typically a larger value)

        frameResult.setVisible(true);
        frameResult.pack();
    };
}

class UI{
    private int numQueens;
    private JFrame frameMain;
    private JTextField textFieldInputQueens;
    private JButton buttonCompute;
    private JPanel wrapperPanel;
    private JPanel panelLivePreview;
    private JPanel[][] panelSquares;
    private Algorithm algorithm;
    private int squareLength = 60;
    private ImageIcon queenIcon;
    private JSlider slider;
    
    
    UI(){
        algorithm = new Algorithm(this);
        buildMainFrame();
    };
    
    public int getnumQueens(){return numQueens;};
    public JFrame getframeMain(){return frameMain;};
    public JPanel getpanelLivePreview(){return panelLivePreview;};
    public JPanel[][] getpanelSquares(){return panelSquares;};
    public void renderFrameMain(){frameMain.pack();};
    public ImageIcon getqueenIcon(){return queenIcon;};

    public void computeLivePreview(){
        if(panelLivePreview == null){
            Thread threadbuildPanelLivePreview = new Thread(() -> buildPanelLivePreview());
            threadbuildPanelLivePreview.start();
            try {
                threadbuildPanelLivePreview.join();
            } catch (InterruptedException e) {
            }
        }
        panelLivePreview.revalidate();
        panelLivePreview.repaint();
        
        Thread threadAlgorithmMain = new Thread(() -> algorithm.runAlgorithm());
        threadAlgorithmMain.run();
        try {
            threadAlgorithmMain.join();
        } catch (InterruptedException e) {
        }
    };
                
    public void resetLivePreview(){
        wrapperPanel.remove(panelLivePreview);
        wrapperPanel.revalidate();
        wrapperPanel.repaint();
        
        panelLivePreview = null;
        panelSquares = null;

        frameMain.remove(wrapperPanel);
        frameMain.revalidate();
        frameMain.repaint();
        wrapperPanel = null;
    }

    public JPanel buildPanelHeader(){
        JPanel panelHeader = new JPanel();

        JLabel labelTitle = new JLabel("Welcome to the N-Queens Problem Visualizer");
        panelHeader.add(labelTitle);
        
        return panelHeader;
    };

    public void enableInput(){
        buttonCompute.setText("Compute");
        buttonCompute.setEnabled(true);
        textFieldInputQueens.setText("");
        textFieldInputQueens.setEnabled(true);
        System.out.println("Button should display Compute");
        frameMain.pack();
    };
    public JPanel buildPanelInput(){
        JPanel panelInput = new JPanel();
        // panelInput.setLayout(new GridLayout(1, 3)); // 1 row, with 3 columns

        // label
        JLabel labelInputQueens = new JLabel("Enter the number of Queens: ");
        panelInput.add(labelInputQueens);

        // input field
        textFieldInputQueens = new JTextField(4);
        panelInput.add(textFieldInputQueens);

        // compute button
        buttonCompute = new JButton();
        buttonCompute.setText("Compute");
        panelInput.add(buttonCompute);

        
        // add functionality to the buttonCompute
        buttonCompute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                String inputString = textFieldInputQueens.getText();
                if(inputString.equals("")){return;};

                if(panelLivePreview != null){resetLivePreview();}
                numQueens = Integer.parseInt(inputString);
                System.out.println("Button should display Computing");
                buttonCompute.setText("Computing");
                buttonCompute.setEnabled(false);
                textFieldInputQueens.setEnabled(false);
                frameMain.pack();
                
                
                Thread thread = new Thread(() -> computeLivePreview());
                thread.start();
            }
        });
        
        return panelInput;
    };

    public void buildPanelLivePreview(){
        int boardSize = numQueens;
        int boardLength = numQueens*squareLength;
        Color lightColor = new Color(255, 255, 255);
        Color darkColor = new Color(139, 69, 19);
        
        panelLivePreview = new JPanel();
        panelLivePreview.setLayout(new GridLayout(numQueens, numQueens));
        panelLivePreview.setPreferredSize(new Dimension(boardLength, boardLength));
        
        panelSquares = new JPanel[boardSize][boardSize];
        for(int indexRank=0 ; indexRank<boardSize ; indexRank++){
            for(int indexFile=0 ; indexFile<boardSize ; indexFile++){
                JPanel panelSquare = new JPanel();
                panelSquare.setPreferredSize(new Dimension(squareLength, squareLength));
                if((indexRank+indexFile)%2 == 0){
                    panelSquare.setBackground(lightColor);
                }else{
                    panelSquare.setBackground(darkColor);
                };
                panelSquare.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panelLivePreview.add(panelSquare);
                panelSquares[indexRank][indexFile] = panelSquare;
            };
        };

        wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperPanel.add(panelLivePreview);
        frameMain.add(wrapperPanel, BorderLayout.CENTER);
        frameMain.pack();
    };

    public void buildMainFrame(){
        frameMain = new JFrame("N-Queens Problem Visualizer");
        frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frameMain.setLayout(new GridLayout(2, 1));  // 3 rows, with 1 column each
        frameMain.setLayout(new BorderLayout());
        frameMain.setPreferredSize(new Dimension(1366, 720));
        frameMain.setVisible(true);

        // build panels
        // JPanel panelHeader = buildPanelHeader();
        JPanel panelInput = buildPanelInput();
        
        // build sliders
        int maxDelayms = 1000;
        slider = new JSlider(JSlider.HORIZONTAL, 0, maxDelayms-1, maxDelayms/2);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                algorithm.setdelaymsBetweenHighlights(maxDelayms-value);
                // System.out.println(maxDelayms-value);    // [#Debugger]
            }
        });

        // set image
        ImageIcon originalIcon  = new ImageIcon("crown.png");
        Image image = originalIcon .getImage();
        Image scaledImage = image.getScaledInstance(squareLength-20, squareLength-20, Image.SCALE_SMOOTH);
        queenIcon = new ImageIcon(scaledImage);

        // add panels
        // frameMain.add(panelHeader);
        frameMain.add(panelInput, BorderLayout.NORTH);
        frameMain.add(slider, BorderLayout.EAST);
        frameMain.pack();
    };
}

public class Main{
    public static void main(String[] args){
        UI ui = new UI();
    }
}