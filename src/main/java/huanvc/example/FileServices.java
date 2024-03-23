package huanvc.example;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

import huanvc.example.ExcelService;

public class FileServices {
    public String currentFileChosen = "";
    public String currentPath = new java.io.File(".").getCanonicalPath() + "/src";

    public FileServices() throws IOException {
    }

    public void FileChooser() throws IOException {
        System.out.println("Current dir:" + currentPath);

        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");

        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);

        String file = dialog.getFile();
        String fileDirectory = dialog.getDirectory();

        System.out.println("File Directory: " + fileDirectory);
        dialog.dispose();

        if (file == null) {
            System.out.println("No file chosen.");
            return;
        } else {
            CopyFile(fileDirectory, file, currentPath);
        }
        System.out.println(file + " chosen.");
    }

    public void ListMenu() throws IOException {
        System.out.println("File menu chosen:");
        Scanner sc = new Scanner(System.in);
        System.out.println("1. Chose File");
        if (currentFileChosen != "") {
            System.out.println("2. Read");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Add");
            // 1 export data from DB
            // 2 export data from file
            // 3 export data from inputs
        }
        System.out.println("6. Export");
        System.out.println("7. Close");
        int a = Integer.parseInt(sc.nextLine());
        ChoseAction(a);
    }

    public void ChoseAction(int a) throws IOException {
        ExcelService ExcelService = new ExcelService();
        switch (a) {
            case 1:
                FileChooser();
                ListMenu();
                break;
            case 2:
                System.out.println("File Reader." + currentFileChosen);
                if (currentFileChosen != "") {
                    showDataReadFromFile();
                }
                ListMenu();
                break;
            case 3:
                System.out.println("Path Update: " + currentFileChosen);
                String id = inputId();
                Integer indexRow = checkExitsId(id);
                if (indexRow != 0) {
                    ExcelModel dataUpdate = inputUpdate(indexRow, id);
                    ExcelService.updateRow(dataUpdate, currentFileChosen);
                } else {
                    System.out.println("ID not found.");
                }
                ListMenu();
                break;
            case 4:
                String IdDelete = inputId();
                Integer indexRowDelete = checkExitsId(IdDelete);
                if (indexRowDelete != 0) {
                    ExcelService.deleteRow(indexRowDelete, currentFileChosen);
                    System.out.println("File deleted.");
                } else {
                    System.out.println("ID not found.");
                }
                ListMenu();
                break;
            case 5:
                ExcelModel dataAdd = inputAdd();
                ExcelService.addRow(dataAdd, currentFileChosen);
                System.out.println("File added.");
                ListMenu();
                break;
            case 6:
                System.out.println("Input data before export");
                ArrayList<ExcelModel> listData = inputFormKeyboard();
                exportDataFromDB(listData);
                ListMenu();
                break;
            case 7:
                System.out.println("Service closed.");
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
    }

    public void CopyFile(String fileDirectory, String fileName, String currentPath) throws IOException {
        String fullPath = fileDirectory + fileName;
        // Path of file where data is to copied
        Path pathIn = (Path) Paths.get(fullPath);
        // Path of file whose data is to be copied
        Path pathOut = (Path) Paths.get(currentPath + File.separator + fileName);
        try {
            Path fileCopy = Files.copy(pathIn, pathOut, StandardCopyOption.REPLACE_EXISTING);
            currentFileChosen = fileCopy.toString();
            System.out.println("File copied." + fileCopy);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void showDataReadFromFile() {
        try {
            ArrayList<ExcelModel> ListData = (ArrayList<ExcelModel>) ExcelService.openFile(currentFileChosen);
            System.out.println("ID  |   Start type  |   Count  |   Proportion");
            for (int i = 0; i < ListData.size(); i++) {
                ExcelModel model = ListData.get(i);
                System.out.println(model.getId() + "     " + model.getStart_type() + "         " + model.getCount() + "        " + model.getProportion());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String inputId() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter ID: ");
        return sc.nextLine();
    }

    public Integer checkExitsId(String id) {
        try {
            ArrayList<ExcelModel> ListData = (ArrayList<ExcelModel>) ExcelService.openFile(currentFileChosen);
            for (int i = 0; i < ListData.size(); i++) {
                ExcelModel model = ListData.get(i);
                if (model.getId().equals(id)) {
                    return (Integer) model.getRow_index();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ExcelModel inputUpdate(Integer indexRow, String id) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Start type to update: ");
        String start_type = sc.nextLine();
        System.out.println("Enter Count to update: ");
        String count = sc.nextLine();
        System.out.println("Enter Proportion to update: ");
        String proportion = sc.nextLine();

        ExcelModel model = new ExcelModel();
        model.setId(id);
        model.setStart_type(start_type);
        model.setCount(count);
        model.setProportion(proportion);
        model.setRow_index(indexRow);
        return model;
    }

    public ExcelModel inputAdd() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter ID: ");
        String id = sc.nextLine();
        System.out.println("Enter Start type: ");
        String start_type = sc.nextLine();
        System.out.println("Enter Count: ");
        String count = sc.nextLine();
        System.out.println("Enter Proportion: ");
        String proportion = sc.nextLine();

        ExcelModel model = new ExcelModel();
        model.setId(id);
        model.setStart_type(start_type);
        model.setCount(count);
        model.setProportion(proportion);
        return model;
    }

    public ArrayList<ExcelModel> inputFormKeyboard() {
        ArrayList<ExcelModel> listInput = new ArrayList<>();

        while (true) {
            if (listInput.size() > 0) {
                Scanner sc = new Scanner(System.in);
                System.out.println("Do you want to add more data? (Y/N)");
                if (sc.nextLine().toLowerCase().equals("n")) {
                    break;
                }
            }
            ExcelModel item = inputAdd();
            listInput.add(item);
        }
        return listInput;
    }

    public void exportDataFromDB(ArrayList<ExcelModel> listData) throws IOException {
        System.out.println("Export data from DB.");
        ExcelService excelService = new ExcelService();
        excelService.exportDataFormStorage(listData);
        return;
    }
}

