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
    
    let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/"
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = UserDefaults.standard.object(forKey: "selectedCourse") as? String
        
        studentNameLabel.text = TutorViewScheduleTableViewController.selectedAppointment.studentName
        dateLabel.text = TutorViewScheduleTableViewController.selectedAppointment.date
        durationLabel.text = TutorViewScheduleTableViewController.selectedAppointment.duration
        subjectLabel.text = TutorViewScheduleTableViewController.selectedAppointment.subject
        acceptStartButton.setTitle(TutorViewScheduleTableViewController.selectedAppointment.buttonLabel, for: .normal)
        
        self.view.backgroundColor = UIColor(patternImage: #imageLiteral(resourceName: "background"))
        
        acceptStartButton.layer.cornerRadius = 5
        acceptStartButton.layer.borderWidth = 1
    }

    @IBAction func buttonPressed(_ sender: Any) {
        if (acceptStartButton.titleLabel?.text == "Start Session")
        {
            startSession()
        }
        else{
            acceptRequest()
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /**
     * This method starts a session when the tutor is looking at a confirmed schedule request.
     */
    func startSession()
    {
        print(dateLabel.text!)
//        let dateFormatter = DateFormatter()
//        dateFormatter.dateFormat = "MM/dd/yyyy hh:mm:ss a" //Your date format
//        let date = dateFormatter.date(from: dateLabel.text!) //according to date format your date string
//        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm" //Your New Date format as per requirement change it own
//        let newDate = dateFormatter.string(from: date!)
        
        // Set up the post request
        let requestURL = URL(string: server + "startscheduledtutorsessiontutor")
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"course\":\"\(course)\",\"dateTime\":\"\(dateLabel.text!)\"}"

        print(postParameters)
        
        // Adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        // Creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                return;
            }
            
            let r = response as? HTTPURLResponse
            
            if (r?.statusCode == 200)
            {
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "startSession", sender: "Success")
                }
            }
            else{
                print(r?.statusCode as Any)
            }
        }
        // Executing the task
        task.resume()
    
    }
    
    /**
     * This method accepts a session request when the tutor is looking at an appointment request.
     */
    func acceptRequest()
    {
        // Set up the post request
        let requestURL = URL(string: server + "acceptstudentscheduledrequest")
        let request = NSMutableURLRequest(url: requestURL! as URL)
        request.httpMethod = "POST"
        
        // Create the post parameters
        let defaults = UserDefaults.standard
        let userEmail = defaults.object(forKey: "userEmail") as! String
        let userToken = defaults.object(forKey: "userToken") as! String
        let course = defaults.object(forKey: "selectedCourse") as! String
        let postParameters = "{\"userEmail\":\"\(userEmail)\",\"userToken\":\"\(userToken)\",\"studentEmail\":\"\(studentNameLabel.text! as String)\",\"course\":\"\(course)\"}"
        
        // Adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        // Creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                return;
            }
                        
            let r = response as? HTTPURLResponse
            
            if (r?.statusCode == 200)
            {
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "acceptRequest", sender: "Success")
                }
            }
            else{
                print(r?.statusCode as Any)
            }
        }
        //executing the task
        task.resume()
    }
}
