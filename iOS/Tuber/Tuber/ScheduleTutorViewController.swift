//
//  ScheduleTutorViewController.swift
//  Tuber
//
//  Created by Anne on 12/4/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class ScheduleTutorViewController: UIViewController {

    @IBOutlet weak var dateDatePicker: UIDatePicker!
    @IBOutlet weak var durationTextField: UITextField!
    @IBOutlet weak var subjectTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        dateDatePicker.minimumDate = Date()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func confirmButtonPress(_ sender: Any) {
        let format = DateFormatter()
        format.dateStyle = .short
        format.timeStyle = .short
        
        let date = format.string(from: dateDatePicker.date)
        let duration =  durationTextField.text
        let subject = subjectTextField.text
        let toPass = date + " " + duration! + " " + subject!
        
        print(toPass)
        
        performSegue(withIdentifier: "scheduleConfirmation", sender: toPass)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "scheduleConfirmation"
        {
            if let destination = segue.destination as? ScheduleTutorConfirmViewController
            {
                destination.passed = sender as? String
            }
        }
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
