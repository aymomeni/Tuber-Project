//
//  TutorServicesViewController.swift
//  Tuber
//
//  Created by Anne on 12/7/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class TutorServicesViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    @IBOutlet weak var servicesTableView: UITableView!

    var icons = [#imageLiteral(resourceName: "immediaterequest"), #imageLiteral(resourceName: "scheduletutor"), #imageLiteral(resourceName: "viewschedule")]
    var names = ["Immediate Request", "Schedule Tutor", "View Schedule"]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = ClassListViewController.selectedClass.className
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 3
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "tutorServices", for: indexPath) as! TutorServicesTableViewCell
        
        cell.optionIconImageView.image = icons[indexPath.row]
        cell.optionNameLabel.text = names[indexPath.row]
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let indexPath = tableView.indexPathForSelectedRow //optional, to get from any UIButton for example
        
        let currentCell = tableView.cellForRow(at: indexPath!)! as! TutorServicesTableViewCell
        
        let selectedOption = currentCell.optionNameLabel.text
        
        if selectedOption == "Immediate Request"
        {
            performSegue(withIdentifier: "immediateRequest", sender: selectedOption)
            
        }
        else if selectedOption == "Schedule Tutor"
        {
            performSegue(withIdentifier: "scheduleTutor", sender: selectedOption)
            
        }
        
    }

}
