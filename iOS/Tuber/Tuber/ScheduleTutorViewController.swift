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
        
        self.title = UserDefaults.standard.object(forKey: "selectedCourse") as? String

        dateDatePicker.minimumDate = Date()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func confirmButtonPress(_ sender: Any) {
        let format = DateFormatter()
        format.dateFormat = "yyyy-MM-dd HH:mm"
        
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        
        let datetime = format.string(from: dateDatePicker.date)
        let duration =  durationTextField.text
        let subject = subjectTextField.text
        let toPass = "Date/Time: " + datetime + " \nDuration (Hours): " + duration! + " \nSubject: " + subject!

     
        //creating the post parameter by concatenating the keys and values from text field
        var postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"" + course + "\",\"topic\":\"" + subject!
        
        postParameters += "\",\"dateTime\":\"" + datetime + "\",\"duration\":\"" + duration! + "\"}"
        
        print(datetime)
        
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
