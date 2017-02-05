//
//  OfferTutorTableViewController.swift
//  Tuber
//
//  Created by Anne on 12/7/16.
//  Copyright © 2016 Tuber. All rights reserved.
//

import UIKit

class OfferTutorTableViewController: UITableViewController {
    
    let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/"
    
    var studentNames: [[String]] = [[],[]]
    var dates: [[String]] = [[],[]]
    var durations: [[String]] = [[],[]]
    var topics: [[String]] = [[],[]]

    override func viewDidLoad() {
        super.viewDidLoad()

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
        return 2
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
    
        override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
            if segue.identifier == "tutorViewSchedule"
            {
                print("first list")
                print(studentNames)
                
                scheduledAppointments()
                
                print("second list")
                print(studentNames)
                
                appointmentRequests()
                
                print("third list")
                print(studentNames)
    
                if let destination = segue.destination as? TutorViewScheduleTableViewController
                {
                    destination.students = studentNames
                    destination.dates = dates
                    destination.duration = durations
                    destination.subjects = topics
                    //destination.passed = sender as? String
                }
            }
        }
    
    func scheduledAppointments()
    {
        
        let semaphore = DispatchSemaphore(value: 0)
        //created NSURL
        let requestURL = NSURL(string: server + "findallscheduletutoracceptedrequests")
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"" + userEmail + "\",\"userToken\":\"" + userToken + "\",\"course\":\"" + course + "\"}"
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            semaphore.signal();
            
//            let r = response as? HTTPURLResponse
            
            //parsing the response
            do {
                //print(response)
                let hotspots = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = hotspots["tutorRequestItems"] {
                    print(arrJSON.count)
                    if (arrJSON.count > 0)
                    {
                    for index in 0...arrJSON.count-1 {
                        
                        let aObject = arrJSON[index] as! [String : AnyObject]
                        
                        self.dates[0].append(aObject["dateTime"] as! String)
                        self.durations[0].append(aObject["duration"] as! String)
                        self.studentNames[0].append(aObject["studentEmail"] as! String)
                        self.topics[0].append(aObject["topic"] as! String)
                        
                        //                        print(aObject["hotspotID"] as! String)
                        //                        print(aObject["student_count"] as! String)
                    }
                    }
                }
                
            } catch {
                print(error)
            }            
        }
        //executing the task
        task.resume()
        semaphore.wait(timeout: .distantFuture);
    }
    
    func appointmentRequests()
    {
        let semaphore = DispatchSemaphore(value: 0)
        
        
        //created NSURL
        let requestURL = NSURL(string: server + "findallscheduletutorrequests")
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        let userEmail = UserDefaults.standard.object(forKey: "userEmail") as! String
        let userToken = UserDefaults.standard.object(forKey: "userToken") as! String
        let course = UserDefaults.standard.object(forKey: "selectedCourse") as! String
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"" + userEmail + "\",\"userToken\":\"" + userToken + "\",\"course\":\"" + course + "\"}"
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        print(postParameters)
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            semaphore.signal();
            
            let r = response as? HTTPURLResponse
            
            //parsing the response
            do {
                //print(response)
                let hotspots = try JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.allowFragments) as! [String : AnyObject]
                
                //self.returnedJSON = hotspots["studyHotspots"] as! [String : AnyObject]{
                if let arrJSON = hotspots["tutorRequestItems"] {
                    print(arrJSON.count)
                    if (arrJSON.count > 0)
                    {
                    for index in 0...arrJSON.count-1 {
                        
                        let aObject = arrJSON[index] as! [String : AnyObject]
                        
                        //print(aObject)
                        
                        
                        self.dates[1].append(aObject["dateTime"] as! String)
                        self.durations[1].append(aObject["duration"] as! String)
                        self.studentNames[1].append(aObject["studentEmail"] as! String)
                        self.topics[1].append(aObject["topic"] as! String)
                        
                    }
                    }
                }
                
                
                
                //self.tableView.reloadData()
                //                //converting resonse to NSDictionary
                //                let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as? NSDictionary
                //
                //                //parsing the json
                //                if let parseJSON = myJSON {
                //
                //                    //creating a string
                //                    var msg : String!
                //
                //                    //getting the json response
                //                    msg = parseJSON["message"] as! String?
                //
                //                    //printing the response
                //                    print(msg)
                //
                //                }
            } catch {
                print(error)
            }
            
            //            if (r?.statusCode == 200)
            //            {
            //                OperationQueue.main.addOperation{
            //                    self.performSegue(withIdentifier: "scheduleConfirmed", sender: "Success")
            //                }
            //            }
            //            else{
            //                print(r?.statusCode as Any)
            //            }
            
        }
        //executing the task
        task.resume()
        semaphore.wait(timeout: .distantFuture);
    }


}
