//
//  TutorViewScheduleTableViewController.swift
//  Tuber
//
//  Created by Anne on 12/7/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class TutorViewScheduleTableViewController: UITableViewController {

    var students = ["Sally Hart", "Michael Fitz", "James Clark"]
    var dates = ["12/10/2016 4:30PM", "12/12/2016 10:45AM", "12/12/2016 2:00PM"]
    var duration = ["1.5", "1", "2"]
    var subjects = ["Malloc", "Proxy Server", "Concurrency"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = ClassListViewController.selectedClass.className

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 3
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "studentAppointments", for: indexPath) as! TutorViewScheduleTableViewCell
        
        cell.studentNameLabel.text = students[indexPath.row]
        cell.dateLabel.text = dates[indexPath.row]
        cell.durationLabel.text = duration[indexPath.row]
        cell.subjectLabel.text = subjects[indexPath.row]
        
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! TutorViewScheduleTableViewCell
        
        selectedAppointment.studentName = currentCell.studentNameLabel.text!
        selectedAppointment.date = currentCell.dateLabel.text!
        selectedAppointment.duration = currentCell.durationLabel.text!
        selectedAppointment.subject = currentCell.subjectLabel.text!
        
        performSegue(withIdentifier: "selectAppointment", sender: nil)
        
        
        
    }
    
    struct selectedAppointment {
        static var studentName = String()
        static var date = String()
        static var duration = String()
        static var subject = String()
    }

    /*
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reuseIdentifier", for: indexPath)

        // Configure the cell...

        return cell
    }
    */

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}