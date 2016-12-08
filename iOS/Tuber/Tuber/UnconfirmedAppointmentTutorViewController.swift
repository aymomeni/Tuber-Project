//
//  UnconfirmedAppointmentTutorViewController.swift
//  Tuber
//
//  Created by Anne on 12/7/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class UnconfirmedAppointmentTutorViewController: UIViewController {

    @IBOutlet weak var studentNameLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var durationLabel: UILabel!
    @IBOutlet weak var subjectLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = ClassListViewController.selectedClass.className
        
        studentNameLabel.text = TutorViewScheduleTableViewController.selectedAppointment.studentName
        dateLabel.text = TutorViewScheduleTableViewController.selectedAppointment.date
        durationLabel.text = TutorViewScheduleTableViewController.selectedAppointment.duration
        subjectLabel.text = TutorViewScheduleTableViewController.selectedAppointment.subject
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
