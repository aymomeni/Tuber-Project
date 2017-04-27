//
//  StudentViewScheduleTableViewController.swift
//  Tuber
//
//  Created by Anne on 2/9/17.
//  Copyright © 2017 Tuber. All rights reserved.
//

import UIKit

class StudentViewScheduleTableViewController: UITableViewController {

    let sections = ["Confirmed Appointments", "Appointment Requests"]
    
    // Set on TutorServicesViewController
    var dates: [[String]] = []
    var duration: [[String]] = []
    var subjects: [[String]] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = UserDefaults.standard.object(forKey: "selectedCourse") as? String
        
        self.navigationController?.willMove(toParentViewController: TutorServicesViewController())
        
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
        self.tableView.separatorStyle = .none
        
//        self.navigationItem.hidesBackButton = true
//        let newBackButton = UIBarButtonItem(title: "< Back", style: UIBarButtonItemStyle.plain, target: self, action: #selector(StudentViewScheduleTableViewController.back(sender:)))
//        self.navigationItem.leftBarButtonItem = newBackButton
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 2
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return dates[section].count
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return sections[section]
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "studentAppointments", for: indexPath) as! TutorViewScheduleTableViewCell
        
        cell.dateLabel.text = dates[indexPath.section][indexPath.row]
        cell.durationLabel.text = duration[indexPath.section][indexPath.row]
        cell.subjectLabel.text = subjects[indexPath.section][indexPath.row]
        
        // Creates separation between cells
        cell.contentView.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
        let whiteRoundedView : UIView = UIView(frame: CGRect(x: 10, y: 10, width: self.view.frame.size.width - 20, height: 80))
        whiteRoundedView.layer.backgroundColor = CGColor(colorSpace: CGColorSpaceCreateDeviceRGB(), components: [1.0, 1.0, 1.0, 1.0])
        whiteRoundedView.layer.masksToBounds = false
        whiteRoundedView.layer.cornerRadius = 3.0
        whiteRoundedView.layer.shadowOffset = CGSize(width: -1, height: 1)
        whiteRoundedView.layer.shadowOpacity = 0.5
        cell.contentView.addSubview(whiteRoundedView)
        cell.contentView.sendSubview(toBack: whiteRoundedView)
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int){
        view.tintColor = UIColor.darkGray
        let header = view as! UITableViewHeaderFooterView
        header.textLabel?.textColor = UIColor.white
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! TutorViewScheduleTableViewCell
        if (indexPath?.section == 0)
        {
            var toPass = [String]()
            
            toPass.append(currentCell.dateLabel.text!)
            toPass.append(currentCell.durationLabel.text!)
            toPass.append(currentCell.subjectLabel.text!)
            
            performSegue(withIdentifier: "sessionInfo", sender: toPass)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "sessionInfo"
        {
            let appointmentInfo = sender as! [String]
            
            if let destination = segue.destination as? StudentStartScheduledViewController
            {
                destination.date = appointmentInfo[0]
                destination.duration = appointmentInfo[1]
                destination.subject = appointmentInfo[2]
            }
        }
    }
}
