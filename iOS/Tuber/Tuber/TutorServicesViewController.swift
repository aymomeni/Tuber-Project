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

        self.title = UserDefaults.standard.object(forKey: "selectedCourse") as? String
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
        else
        {
            print("view student schedule")
            prepStudentSchedule()
        }
        
    }
    
    func prepStudentSchedule()
    {
        //created NSURL
        let requestURL = NSURL(string: "http://tuber-test.cloudapp.net/ProductRESTService.svc/checkscheduledpairedstatus")
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"" + userEmail + "\",\"userToken\":\"" + userToken + "\"}"
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print(error as! String)
                return;
            }
            
            //            let r = response as? HTTPURLResponse
            
            //parsing the response
            do {
                //print(response)
                let requests = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                var dates = [[String](),[String]()]
                var durations = [[String](),[String]()]
                var topics = [[String](),[String]()]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = requests["requests"] {
//                    print(arrJSON.count)
                    if (arrJSON.count > 0)
                    {
                        for index in 0...arrJSON.count-1 {
                            
                            let aObject = arrJSON[index] as! [String : AnyObject]
                            
                            if ((aObject["course"] as! String) == course){
                                if (aObject["isPaired"] as! Bool == true){
                                    dates[0].append(aObject["dateTime"] as! String)
                                    durations[0].append(aObject["duration"] as! String)
                                    topics[0].append(aObject["topic"] as! String)
                                }
                                else{
                                    dates[1].append(aObject["dateTime"] as! String)
                                    durations[1].append(aObject["duration"] as! String)
                                    topics[1].append(aObject["topic"] as! String)
                                }
                                
                                
                            }
                        }
                        
                    }
                }
                OperationQueue.main.addOperation{
//                    print(topics)
                    var toSend = [[[String]]]()
                    toSend.append(dates)
                    toSend.append(durations)
                    toSend.append(topics)
                    
                    print(toSend.count)
                    
//                    print(toSend)
                    self.performSegue(withIdentifier: "studentViewSchedule", sender: toSend)
                }
                
                return;
                
            } catch {
                print(error)
            }
        }
        //executing the task
        task.resume()
        //        semaphore.wait(timeout: .distantFuture);
    }

    

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "studentViewSchedule"
        {
            let appointmentInfo = sender as! [[[String]]]
            print(appointmentInfo[0])
            print(appointmentInfo[1])
            print(appointmentInfo[2])
            
            if let destination = segue.destination as? StudentViewScheduleTableViewController
            {
                destination.dates = appointmentInfo[0]
                destination.duration = appointmentInfo[1]
                destination.subjects = appointmentInfo[2]
                //destination.passed = sender as? String
            }
        }
    }
    
    
}
