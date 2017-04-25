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
    @IBOutlet weak var confirmButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = UserDefaults.standard.object(forKey: "selectedCourse") as? String

        dateDatePicker.minimumDate = Date()
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
        
        confirmButton.layer.cornerRadius = 5
        confirmButton.layer.borderWidth = 1
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /**
     * This function prepares the post parameters and the message displayed for the confirmation page.
     */
    @IBAction func confirmButtonPress(_ sender: Any) {
        
        // Set up the different aspects of the message and post parameter.
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        let duration =  durationTextField.text
        let subject = subjectTextField.text
        
        let format = DateFormatter()
        format.dateFormat = "yyyy-MM-dd HH:mm"
        let datetime = format.string(from: dateDatePicker.date)
        
        // The confirmation message for the confirmation page
        let toPass = "Date/Time: " + datetime + " \nDuration (Hours): " + duration! + " \nSubject: " + subject!
        
        // Post parameter setup if the user confirms on the confirmation page.
        var postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"" + course + "\",\"topic\":\"" + subject!
        postParameters += "\",\"dateTime\":\"" + datetime + "\",\"duration\":\"" + duration! + "\"}"

        // Set up the sender for the segue
        var send: [String] = []
        send.append(toPass)
        send.append(postParameters)
        
        performSegue(withIdentifier: "scheduleConfirmation", sender: send)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "scheduleConfirmation"
        {
            if let destination = segue.destination as? ScheduleTutorConfirmViewController
            {
                destination.passed = sender as! [String]
            }
        }
    }


}
