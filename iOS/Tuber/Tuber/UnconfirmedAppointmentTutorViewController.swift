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
    
    @IBOutlet weak var acceptStartButton: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = UserDefaults.standard.object(forKey: "selectedCourse") as? String
        
        studentNameLabel.text = TutorViewScheduleTableViewController.selectedAppointment.studentName
        dateLabel.text = TutorViewScheduleTableViewController.selectedAppointment.date
        durationLabel.text = TutorViewScheduleTableViewController.selectedAppointment.duration
        subjectLabel.text = TutorViewScheduleTableViewController.selectedAppointment.subject
        acceptStartButton.setTitle(TutorViewScheduleTableViewController.selectedAppointment.buttonLabel, for: .normal)
    }

    @IBAction func buttonPressed(_ sender: Any) {
        if (acceptStartButton.titleLabel?.text == "Start Session")
        {
            print("start")
        }
        else{
            print("accept")
            performSegue(withIdentifier: "acceptRequest", sender: nil)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func acceptRequest()
    {
        
    }
}
