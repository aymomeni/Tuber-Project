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

        // Do any additional setup after loading the view.
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
        
        //        let toPass = currentCell.textLabel!.text
        let toPass = currentCell.optionNameLabel.text
        
        if toPass == "Schedule Tutor"
        {
            performSegue(withIdentifier: "scheduleTutor", sender: toPass)
            
        }
        
    }

}
